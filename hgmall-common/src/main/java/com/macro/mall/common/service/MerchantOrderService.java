package com.macro.mall.common.service;

import com.macro.mall.common.constant.OrderStatus;
import com.macro.mall.common.domain.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商家订单服务
 * 提供商家订单管理功能
 */
@Service
public class MerchantOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ShopService shopService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String MERCHANT_ORDERS_KEY_PREFIX = "merchant:orders:";
    private static final String ORDER_DETAIL_KEY_PREFIX = "merchant:order:detail:";
    private static final String ORDER_STATISTICS_KEY_PREFIX = "merchant:order:statistics:";

    /**
     * 缓存TTL（秒）
     */
    private static final long ORDERS_CACHE_TTL = 60; // 1分钟
    private static final long ORDER_DETAIL_CACHE_TTL = 300; // 5分钟
    private static final long STATISTICS_CACHE_TTL = 300; // 5分钟

    /**
     * 获取商家订单列表（分页、筛选）
     * 
     * @param merchantId 商家ID
     * @param shopId 店铺ID（可选，不传则查询所有店铺）
     * @param status 订单状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 订单列表
     * @throws RuntimeException 查询失败时抛出异常
     */
    public MerchantOrderListVO getMerchantOrders(Long merchantId, Long shopId, Integer status,
                                                 LocalDateTime startTime, LocalDateTime endTime,
                                                 Integer page, Integer size) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法查看订单");
        }

        // 获取商家所有店铺ID
        List<Long> shopIds = getMerchantShopIds(merchantId, shopId);

        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建缓存Key
        String cacheKey = buildOrdersCacheKey(merchantId, shopId, status, startTime, endTime, page, size);

        // 尝试从缓存获取
        MerchantOrderListVO cached = (MerchantOrderListVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询订单列表
        List<Map<String, Object>> orderMaps = orderMapper.selectMerchantOrdersWithPaging(
                shopIds, status, startTime, endTime, offset, size);
        Long total = orderMapper.countMerchantOrders(shopIds, status, startTime, endTime);

        // 转换为VO
        List<MerchantOrderVO> orders = convertToMerchantOrderVOList(orderMaps);

        // 构建返回结果
        MerchantOrderListVO result = new MerchantOrderListVO();
        result.setOrders(orders);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        // 存入缓存
        long ttl = ORDERS_CACHE_TTL + new Random().nextInt(30);
        redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @param merchantId 商家ID（用于验证权限）
     * @return 订单详情
     * @throws RuntimeException 查询失败时抛出异常
     */
    public MerchantOrderVO getOrderDetail(Long orderId, Long merchantId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法查看订单");
        }

        // 构建缓存Key
        String cacheKey = ORDER_DETAIL_KEY_PREFIX + orderId;

        // 尝试从缓存获取
        MerchantOrderVO cached = (MerchantOrderVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            // 验证权限
            validateOrderAccess(orderId, merchantId);
            return cached;
        }

        // 验证权限
        validateOrderAccess(orderId, merchantId);

        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 转换为VO
        MerchantOrderVO orderVO = convertToMerchantOrderVO(order);

        // 存入缓存
        long ttl = ORDER_DETAIL_CACHE_TTL + new Random().nextInt(60);
        redisTemplate.opsForValue().set(cacheKey, orderVO, ttl, TimeUnit.SECONDS);

        return orderVO;
    }

    /**
     * 订单发货
     * 
     * @param orderId 订单ID
     * @param merchantId 商家ID（用于验证权限）
     * @param remark 备注（可选）
     * @return 更新后的订单信息
     * @throws RuntimeException 操作失败时抛出异常
     */
    @Transactional
    public MerchantOrderVO shipOrder(Long orderId, Long merchantId, String remark) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法发货");
        }

        // 验证权限
        validateOrderAccess(orderId, merchantId);

        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态（只有已支付订单才能发货）
        if (order.getStatus() == null || order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("只有已支付订单才能发货");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateByPrimaryKeySelective(order);

        // 发送发货邮件通知
        orderService.sendShippedEmail(order);

        // 清除相关缓存
        clearOrderCache(orderId, order.getShopId(), merchantId);

        // 转换为VO
        return convertToMerchantOrderVO(order);
    }

    /**
     * 获取订单统计信息
     * 
     * @param shopId 店铺ID（0表示查询商家所有店铺的总报表）
     * @param merchantId 商家ID（用于验证权限）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计信息
     * @throws RuntimeException 查询失败时抛出异常
     */
    public OrderStatisticsVO getOrderStatistics(Long shopId, Long merchantId,
                                                LocalDateTime startTime, LocalDateTime endTime) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法查看统计");
        }

        List<Long> shopIds;
        
        if (shopId == null || shopId == 0) {
            // shopId=0 表示查询商家所有店铺的总报表
            ShopExample example = new ShopExample();
            example.createCriteria().andMerchantIdEqualTo(merchantId);
            List<Shop> shops = shopMapper.selectByExample(example);
            shopIds = shops.stream().map(Shop::getShopId).collect(Collectors.toList());
            
            if (shopIds.isEmpty()) {
                // 商家没有店铺，返回空统计
                return createEmptyStatistics();
            }
        } else {
            // 验证店铺所有权
            shopService.validateShopOwnership(shopId, merchantId);
            shopIds = Collections.singletonList(shopId);
        }

        // 构建缓存Key
        String cacheKey = buildStatisticsCacheKey(shopId, merchantId, startTime, endTime);

        // 尝试从缓存获取
        OrderStatisticsVO cached = (OrderStatisticsVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询统计信息
        Map<String, Object> stats = orderMapper.selectOrderStatisticsByShopIds(shopIds, startTime, endTime);

        // 转换为VO
        OrderStatisticsVO result = convertToOrderStatisticsVO(stats);

        // 存入缓存
        long ttl = STATISTICS_CACHE_TTL + new Random().nextInt(60);
        redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 创建空统计信息
     */
    private OrderStatisticsVO createEmptyStatistics() {
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setTotalOrders(0L);
        vo.setPaidOrders(0L);
        vo.setShippedOrders(0L);
        vo.setCompletedOrders(0L);
        vo.setCancelledOrders(0L);
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setPaidAmount(BigDecimal.ZERO);
        vo.setCompletedAmount(BigDecimal.ZERO);
        return vo;
    }

    /**
     * 获取商家所有店铺ID
     */
    private List<Long> getMerchantShopIds(Long merchantId, Long shopId) {
        if (shopId != null) {
            // 验证店铺所有权
            shopService.validateShopOwnership(shopId, merchantId);
            return Collections.singletonList(shopId);
        } else {
            // 查询商家所有店铺
            ShopExample example = new ShopExample();
            example.createCriteria().andMerchantIdEqualTo(merchantId);
            List<Shop> shops = shopMapper.selectByExample(example);
            return shops.stream().map(Shop::getShopId).collect(Collectors.toList());
        }
    }

    /**
     * 验证订单访问权限
     */
    private void validateOrderAccess(Long orderId, Long merchantId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证店铺是否属于该商家
        Shop shop = shopMapper.selectByPrimaryKey(order.getShopId());
        if (shop == null || !shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权访问该订单");
        }
    }

    /**
     * 构建订单列表缓存Key
     */
    private String buildOrdersCacheKey(Long merchantId, Long shopId, Integer status,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       Integer page, Integer size) {
        StringBuilder key = new StringBuilder(MERCHANT_ORDERS_KEY_PREFIX);
        key.append(merchantId).append(":");
        key.append(shopId != null ? shopId : "all").append(":");
        key.append(status != null ? status : "all").append(":");
        key.append(startTime != null ? startTime.toString() : "all").append(":");
        key.append(endTime != null ? endTime.toString() : "all").append(":");
        key.append(page).append(":").append(size);
        return key.toString();
    }

    /**
     * 构建统计缓存Key
     */
    private String buildStatisticsCacheKey(Long shopId, Long merchantId, LocalDateTime startTime, LocalDateTime endTime) {
        StringBuilder key = new StringBuilder(ORDER_STATISTICS_KEY_PREFIX);
        if (shopId == null || shopId == 0) {
            key.append("merchant:").append(merchantId).append(":all:");
        } else {
            key.append(shopId).append(":");
        }
        key.append(startTime != null ? startTime.toString() : "all").append(":");
        key.append(endTime != null ? endTime.toString() : "all");
        return key.toString();
    }

    /**
     * 清除订单相关缓存
     */
    private void clearOrderCache(Long orderId, Long shopId, Long merchantId) {
        // 清除订单详情缓存
        redisTemplate.delete(ORDER_DETAIL_KEY_PREFIX + orderId);

        // 清除订单列表缓存（该商家的所有相关缓存）
        String pattern = MERCHANT_ORDERS_KEY_PREFIX + merchantId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // 清除统计缓存（包括该店铺和商家所有店铺的统计）
        String statsPattern = ORDER_STATISTICS_KEY_PREFIX + shopId + ":*";
        Set<String> statsKeys = redisTemplate.keys(statsPattern);
        if (statsKeys != null && !statsKeys.isEmpty()) {
            redisTemplate.delete(statsKeys);
        }
        
        // 清除商家所有店铺的统计缓存
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop != null) {
            String merchantStatsPattern = ORDER_STATISTICS_KEY_PREFIX + "merchant:" + shop.getMerchantId() + ":all:*";
            Set<String> merchantStatsKeys = redisTemplate.keys(merchantStatsPattern);
            if (merchantStatsKeys != null && !merchantStatsKeys.isEmpty()) {
                redisTemplate.delete(merchantStatsKeys);
            }
        }
    }

    /**
     * 转换为MerchantOrderVO列表
     */
    private List<MerchantOrderVO> convertToMerchantOrderVOList(List<Map<String, Object>> orderMaps) {
        List<MerchantOrderVO> orders = new ArrayList<>();
        for (Map<String, Object> map : orderMaps) {
            orders.add(convertMapToMerchantOrderVO(map));
        }
        return orders;
    }

    /**
     * 将Map转换为MerchantOrderVO
     */
    private MerchantOrderVO convertMapToMerchantOrderVO(Map<String, Object> map) {
        MerchantOrderVO vo = new MerchantOrderVO();
        vo.setOrderId(((Number) map.get("order_id")).longValue());
        vo.setBatchOrderId(((Number) map.get("batch_order_id")).longValue());
        vo.setUserId(((Number) map.get("user_id")).longValue());
        vo.setShopId(((Number) map.get("shop_id")).longValue());
        vo.setStatus(((Number) map.get("status")).intValue());
        vo.setStatusText(getStatusText(vo.getStatus()));
        vo.setTotal(new BigDecimal(map.get("total").toString()));
        vo.setAddressId(map.get("address_id") != null ? ((Number) map.get("address_id")).longValue() : null);
        vo.setShopName((String) map.get("shop_name"));

        // 脱敏处理用户名和邮箱
        String userName = (String) map.get("user_name");
        vo.setUserName(maskUserName(userName));
        String userEmail = (String) map.get("user_email");
        vo.setUserEmail(maskEmail(userEmail));

        // 时间字段
        if (map.get("create_time") != null) {
            Object createTimeObj = map.get("create_time");
            if (createTimeObj instanceof java.sql.Timestamp) {
                vo.setCreateTime(((java.sql.Timestamp) createTimeObj).toLocalDateTime());
            } else if (createTimeObj instanceof LocalDateTime) {
                vo.setCreateTime((LocalDateTime) createTimeObj);
            }
        }
        if (map.get("update_time") != null) {
            Object updateTimeObj = map.get("update_time");
            if (updateTimeObj instanceof java.sql.Timestamp) {
                vo.setUpdateTime(((java.sql.Timestamp) updateTimeObj).toLocalDateTime());
            } else if (updateTimeObj instanceof LocalDateTime) {
                vo.setUpdateTime((LocalDateTime) updateTimeObj);
            }
        }

        // 查询订单项
        List<MerchantOrderItemVO> orderItems = getOrderItems(vo.getOrderId());
        vo.setOrderItems(orderItems);

        // 查询地址
        if (vo.getAddressId() != null) {
            Address address = addressMapper.selectByPrimaryKey(vo.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                addressVO.setAddressId(address.getAddressId());
                addressVO.setDetail(address.getDetail());
                vo.setAddress(addressVO);
            }
        }

        return vo;
    }

    /**
     * 转换为MerchantOrderVO
     */
    private MerchantOrderVO convertToMerchantOrderVO(Order order) {
        // 查询关联信息
        Shop shop = shopMapper.selectByPrimaryKey(order.getShopId());
        Usr user = usrMapper.selectByPrimaryKey(order.getUserId());

        MerchantOrderVO vo = new MerchantOrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setBatchOrderId(order.getBatchOrderId());
        vo.setUserId(order.getUserId());
        vo.setShopId(order.getShopId());
        vo.setStatus(order.getStatus());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setTotal(order.getTotal());
        vo.setAddressId(order.getAddressId());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        if (shop != null) {
            vo.setShopName(shop.getName());
        }

        if (user != null) {
            vo.setUserName(maskUserName(user.getUserName()));
            vo.setUserEmail(maskEmail(user.getEmail()));
        }

        // 查询订单项
        List<MerchantOrderItemVO> orderItems = getOrderItems(order.getOrderId());
        vo.setOrderItems(orderItems);

        // 查询地址
        if (order.getAddressId() != null) {
            Address address = addressMapper.selectByPrimaryKey(order.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                addressVO.setAddressId(address.getAddressId());
                addressVO.setDetail(address.getDetail());
                vo.setAddress(addressVO);
            }
        }

        return vo;
    }

    /**
     * 获取订单项列表
     */
    private List<MerchantOrderItemVO> getOrderItems(Long orderId) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderItem> items = orderItemMapper.selectByExample(example);

        List<MerchantOrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : items) {
            MerchantOrderItemVO itemVO = new MerchantOrderItemVO();
            itemVO.setOrderId(item.getOrderId());
            itemVO.setItemId(item.getItemId());
            itemVO.setSkuId(item.getSkuId());
            itemVO.setPriceSnapshot(item.getPriceSnapshot());
            itemVO.setNum(item.getNum());
            itemVO.setSubtotal(item.getPriceSnapshot().multiply(new BigDecimal(item.getNum())));

            // 查询SKU和商品信息
            Sku sku = skuMapper.selectByPrimaryKey(item.getSkuId());
            if (sku != null) {
                itemVO.setSpec(sku.getSpec());
                Product product = productMapper.selectByPrimaryKey(sku.getProductId());
                if (product != null) {
                    itemVO.setProductId(product.getProductId());
                    itemVO.setProductName(product.getName());
                    itemVO.setProductImageUrl(product.getImageUrl());
                }
            }

            itemVOs.add(itemVO);
        }

        return itemVOs;
    }

    /**
     * 转换为OrderStatisticsVO
     */
    private OrderStatisticsVO convertToOrderStatisticsVO(Map<String, Object> stats) {
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setTotalOrders(((Number) stats.get("total_orders")).longValue());
        vo.setPaidOrders(((Number) stats.get("paid_orders")).longValue());
        vo.setShippedOrders(((Number) stats.get("shipped_orders")).longValue());
        vo.setCompletedOrders(((Number) stats.get("completed_orders")).longValue());
        vo.setCancelledOrders(((Number) stats.get("cancelled_orders")).longValue());
        vo.setTotalAmount(new BigDecimal(stats.get("total_amount").toString()));
        vo.setPaidAmount(new BigDecimal(stats.get("paid_amount").toString()));
        vo.setCompletedAmount(new BigDecimal(stats.get("completed_amount").toString()));
        return vo;
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            case 5:
                return "退款中";
            case 6:
                return "已退款";
            default:
                return "未知";
        }
    }

    /**
     * 脱敏用户名
     */
    private String maskUserName(String userName) {
        if (userName == null || userName.length() <= 1) {
            return userName;
        }
        if (userName.length() == 2) {
            return userName.charAt(0) + "*";
        }
        return userName.charAt(0) + "***";
    }

    /**
     * 脱敏邮箱
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 2) {
            return prefix.charAt(0) + "***" + suffix;
        }
        return prefix.substring(0, 2) + "***" + suffix;
    }
}

