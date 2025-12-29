package com.macro.mall.admin.service;

import com.macro.mall.common.constant.OrderStatus;
import com.macro.mall.common.domain.MerchantOrderListVO;
import com.macro.mall.common.domain.MerchantOrderVO;
import com.macro.mall.common.domain.OrderStatisticsVO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员订单管理服务
 */
@Service
public class AdminOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 获取订单列表（分页、筛选）
     */
    public MerchantOrderListVO getOrderList(Long shopId, Long merchantId, Integer status,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            String keyword, Integer page, Integer size) {
        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建查询条件
        List<Long> shopIds = new ArrayList<>();
        if (shopId != null) {
            shopIds.add(shopId);
        } else if (merchantId != null) {
            // 查询商家所有店铺
            ShopExample shopExample = new ShopExample();
            shopExample.createCriteria().andMerchantIdEqualTo(merchantId);
            List<Shop> shops = shopMapper.selectByExample(shopExample);
            shopIds = shops.stream().map(Shop::getShopId).collect(Collectors.toList());
        }

        // 查询订单列表
        List<Map<String, Object>> orderMaps = orderMapper.selectMerchantOrdersWithPaging(
                shopIds.isEmpty() ? null : shopIds, status, startTime, endTime, offset, size);

        // 如果有关键词，需要过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            orderMaps = orderMaps.stream()
                    .filter(map -> {
                        String orderId = String.valueOf(map.get("order_id"));
                        String userName = (String) map.get("user_name");
                        return orderId.contains(keywordLower) ||
                                (userName != null && userName.toLowerCase().contains(keywordLower));
                    })
                    .collect(Collectors.toList());
        }

        // 转换为VO
        List<MerchantOrderVO> orders = convertToMerchantOrderVOList(orderMaps);

        // 查询总数（简化处理，实际应该优化SQL）
        Long total = orderMapper.countMerchantOrders(
                shopIds.isEmpty() ? null : shopIds, status, startTime, endTime);

        MerchantOrderListVO result = new MerchantOrderListVO();
        result.setOrders(orders);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        return result;
    }

    /**
     * 获取订单详情
     */
    public MerchantOrderVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

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

        // 管理员可以看到完整信息，不进行脱敏
        if (user != null) {
            vo.setUserName(user.getUserName());
            vo.setUserEmail(user.getEmail());
        }

        // 查询订单项
        List<com.macro.mall.common.domain.MerchantOrderItemVO> orderItems = getOrderItems(orderId);
        vo.setOrderItems(orderItems);

        // 查询地址
        if (order.getAddressId() != null) {
            Address address = addressMapper.selectByPrimaryKey(order.getAddressId());
            if (address != null) {
                com.macro.mall.common.domain.AddressVO addressVO = new com.macro.mall.common.domain.AddressVO();
                addressVO.setAddressId(address.getAddressId());
                addressVO.setDetail(address.getDetail());
                vo.setAddress(addressVO);
            }
        }

        return vo;
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId, String reason, Long adminUserId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有待支付或已支付的订单才能取消
        if (order.getStatus() != OrderStatus.WAIT_PAY && order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("该订单状态不允许取消");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateByPrimaryKeySelective(order);

        // 恢复商品库存
        restoreStock(orderId);

        // 记录审计日志
        recordAuditLog(adminUserId, orderId, "order", "cancel",
                String.format("管理员取消订单[%d]。原因：%s", orderId, reason != null ? reason : "无"));
    }

    /**
     * 获取订单统计
     */
    public OrderStatisticsVO getOrderStatistics(Long shopId, Long merchantId,
                                                LocalDateTime startTime, LocalDateTime endTime) {
        List<Long> shopIds = new ArrayList<>();
        if (shopId != null) {
            shopIds.add(shopId);
        } else if (merchantId != null) {
            ShopExample shopExample = new ShopExample();
            shopExample.createCriteria().andMerchantIdEqualTo(merchantId);
            List<Shop> shops = shopMapper.selectByExample(shopExample);
            shopIds = shops.stream().map(Shop::getShopId).collect(Collectors.toList());
        }

        if (shopIds.isEmpty()) {
            // 查询所有订单
            Map<String, Object> stats = orderMapper.selectOrderStatisticsByShopIds(null, startTime, endTime);
            return convertToOrderStatisticsVO(stats);
        } else {
            Map<String, Object> stats = orderMapper.selectOrderStatisticsByShopIds(shopIds, startTime, endTime);
            return convertToOrderStatisticsVO(stats);
        }
    }

    /**
     * 恢复商品库存
     */
    private void restoreStock(Long orderId) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderItem> items = orderItemMapper.selectByExample(example);

        for (OrderItem item : items) {
            Sku sku = skuMapper.selectByPrimaryKey(item.getSkuId());
            if (sku != null) {
                sku.setStock(sku.getStock() + item.getNum());
                sku.setUpdateTime(LocalDateTime.now());
                skuMapper.updateByPrimaryKeySelective(sku);
            }
        }
    }

    /**
     * 转换为MerchantOrderVO列表
     */
    private List<MerchantOrderVO> convertToMerchantOrderVOList(List<Map<String, Object>> orderMaps) {
        List<MerchantOrderVO> orders = new ArrayList<>();
        for (Map<String, Object> map : orderMaps) {
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

            // 管理员可以看到完整信息，不进行脱敏
            String userName = (String) map.get("user_name");
            vo.setUserName(userName);
            String userEmail = (String) map.get("user_email");
            vo.setUserEmail(userEmail);

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
            List<com.macro.mall.common.domain.MerchantOrderItemVO> orderItems = getOrderItems(vo.getOrderId());
            vo.setOrderItems(orderItems);

            // 查询地址
            if (vo.getAddressId() != null) {
                Address address = addressMapper.selectByPrimaryKey(vo.getAddressId());
                if (address != null) {
                    com.macro.mall.common.domain.AddressVO addressVO = new com.macro.mall.common.domain.AddressVO();
                    addressVO.setAddressId(address.getAddressId());
                    addressVO.setDetail(address.getDetail());
                    vo.setAddress(addressVO);
                }
            }

            orders.add(vo);
        }
        return orders;
    }

    /**
     * 获取订单项列表
     */
    private List<com.macro.mall.common.domain.MerchantOrderItemVO> getOrderItems(Long orderId) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderItem> items = orderItemMapper.selectByExample(example);

        List<com.macro.mall.common.domain.MerchantOrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : items) {
            com.macro.mall.common.domain.MerchantOrderItemVO itemVO = new com.macro.mall.common.domain.MerchantOrderItemVO();
            itemVO.setOrderId(item.getOrderId());
            itemVO.setItemId(item.getItemId());
            itemVO.setSkuId(item.getSkuId());
            itemVO.setPriceSnapshot(item.getPriceSnapshot());
            itemVO.setNum(item.getNum());
            itemVO.setSubtotal(item.getPriceSnapshot().multiply(new BigDecimal(item.getNum())));

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
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUserId, Long targetId, String targetType,
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUserId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }
}

