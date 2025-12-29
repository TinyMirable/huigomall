package com.macro.mall.common.service;

import com.macro.mall.common.constant.OrderStatus;
import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.common.domain.ShopListVO;
import com.macro.mall.common.domain.ShopVO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 店铺服务
 * 提供店铺管理功能
 */
@Service
public class ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String SHOP_DETAIL_KEY_PREFIX = "shop:detail:";
    private static final String MERCHANT_SHOPS_KEY_PREFIX = "merchant:shops:";

    /**
     * 缓存TTL（秒）
     */
    private static final long SHOP_DETAIL_TTL = 300; // 5分钟
    private static final long MERCHANT_SHOPS_TTL = 180; // 3分钟

    /**
     * 创建店铺
     * 
     * @param merchantId 商家ID
     * @param name 店铺名称
     * @param description 店铺描述
     * @return 创建的店铺信息
     * @throws RuntimeException 创建失败时抛出异常
     */
    @Transactional
    public ShopVO createShop(Long merchantId, String name, String description) {
        // 验证商家是否存在
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        // 检查商家状态
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法创建店铺");
        }

        // 检查商家店铺数量（最多3个）
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andMerchantIdEqualTo(merchantId);
        long shopCount = shopMapper.countByExample(shopExample);
        if (shopCount >= 3) {
            throw new RuntimeException("每个商家最多只能创建3个店铺");
        }

        // 检查店铺名称是否已存在（同一商家下）
        ShopExample nameExample = new ShopExample();
        nameExample.createCriteria()
                .andMerchantIdEqualTo(merchantId)
                .andNameEqualTo(name);
        long existingCount = shopMapper.countByExample(nameExample);
        if (existingCount > 0) {
            throw new RuntimeException("店铺名称已存在，请更换其他名称");
        }

        // 创建店铺
        Shop shop = new Shop();
        shop.setMerchantId(merchantId);
        shop.setName(name);
        shop.setDescription(description);
        shop.setStatus(ShopStatus.ACTIVE); // 默认状态为正常营业
        shop.setCreateTime(LocalDateTime.now());
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.insertSelective(shop);

        // 记录审计日志
        recordAuditLog(null, shop.getShopId(), "shop", "create", 
                String.format("商家[%d]创建店铺[%s]", merchantId, name));

        ShopVO result = convertToShopVO(shop);

        // 清除商家店铺列表缓存
        String merchantShopsKey = MERCHANT_SHOPS_KEY_PREFIX + merchantId;
        redisTemplate.delete(merchantShopsKey);

        return result;
    }

    /**
     * 查询商家名下的所有店铺
     * 
     * @param merchantId 商家ID
     * @return 店铺列表
     */
    public List<ShopVO> getShopsByMerchant(Long merchantId) {
        // 验证商家状态（如果商家被禁用，仍然可以查看店铺列表，但无法管理）
        // 这里不验证，允许查看，但在具体操作时会验证
        
        // 构建缓存Key
        String cacheKey = MERCHANT_SHOPS_KEY_PREFIX + merchantId;

        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<ShopVO> cached = (List<ShopVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询数据库
        ShopExample example = new ShopExample();
        example.createCriteria().andMerchantIdEqualTo(merchantId);
        example.setOrderByClause("create_time DESC");
        List<Shop> shops = shopMapper.selectByExample(example);
        
        List<ShopVO> result = shops.stream()
                .map(this::convertToShopVO)
                .collect(Collectors.toList());

        // 存入缓存（随机TTL，2-4分钟）
        long ttl = MERCHANT_SHOPS_TTL + new Random().nextInt(120);
        redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 关闭店铺（逻辑关闭）
     * 使用事务保证原子性
     * 
     * @param merchantId 商家ID
     * @param shopId 店铺ID
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @throws RuntimeException 关闭失败时抛出异常
     */
    @Transactional
    public void closeShop(Long merchantId, Long shopId, Long operatorUserId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法关闭店铺");
        }

        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 验证店铺是否属于该商家
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该店铺");
        }

        // 检查店铺是否已经关闭（CLOSED 或 ADMIN_CLOSED）
        if (shop.getStatus() != null && 
            (shop.getStatus() == ShopStatus.CLOSED || shop.getStatus() == ShopStatus.ADMIN_CLOSED)) {
            throw new RuntimeException("店铺已经关闭");
        }

        // 1. 更新店铺状态为 CLOSED
        shop.setStatus(ShopStatus.CLOSED);
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.updateByPrimaryKeySelective(shop);

        // 2. 将该店铺下所有商品状态改为 OFF_SHELF
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andShopIdEqualTo(shopId);
        List<Product> products = productMapper.selectByExample(productExample);
        
        for (Product product : products) {
            product.setStatus(ProductStatus.OFF_SHELF);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateByPrimaryKeySelective(product);
        }

        // 3. 将该店铺下所有未支付订单（status = WAIT_PAY）改为 CANCELLED
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria()
                .andShopIdEqualTo(shopId)
                .andStatusEqualTo(OrderStatus.WAIT_PAY);
        List<Order> waitPayOrders = orderMapper.selectByExample(orderExample);
        
        for (Order order : waitPayOrders) {
            order.setStatus(OrderStatus.CANCELLED); // 使用 CANCELLED 状态表示关闭
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        
        // 注意：已支付、已发货、已完成的订单保持不变，仍可查询

        // 4. 记录审计日志
        recordAuditLog(operatorUserId, shopId, "shop", "close", 
                String.format("商家[%d]关闭店铺[%s]", merchantId, shop.getName()));

        // 清除相关缓存
        clearShopCache(shopId, merchantId);
    }

    /**
     * 查看店铺（仅 ACTIVE 状态）
     * 
     * @param shopId 店铺ID
     * @return 店铺信息
     * @throws RuntimeException 店铺不存在或非 ACTIVE 状态时抛出异常
     */
    public ShopVO getShop(Long shopId) {
        // 构建缓存Key
        String cacheKey = SHOP_DETAIL_KEY_PREFIX + shopId;

        // 尝试从缓存获取
        ShopVO cached = (ShopVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询数据库
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 只允许查看 ACTIVE 状态的店铺
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺不存在或已关闭");
        }

        ShopVO result = convertToShopVO(shop);

        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, SHOP_DETAIL_TTL, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 验证店铺是否属于商家
     * 
     * @param shopId 店铺ID
     * @param merchantId 商家ID
     * @throws RuntimeException 验证失败时抛出异常
     */
    public void validateShopOwnership(Long shopId, Long merchantId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该店铺");
        }
    }

    /**
     * 检查店铺状态是否允许操作
     * 
     * @param shopId 店铺ID
     * @throws RuntimeException 店铺状态不允许操作时抛出异常
     */
    public void validateShopStatusForOperation(Long shopId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法进行此操作");
        }
    }

    /**
     * 根据用户ID获取商家ID
     * 
     * @param userId 用户ID
     * @return 商家ID
     * @throws RuntimeException 用户不是商家时抛出异常
     */
    public Long getMerchantIdByUserId(Long userId) {
        MerchantExample example = new MerchantExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<Merchant> merchants = merchantMapper.selectByExample(example);
        
        if (merchants == null || merchants.isEmpty()) {
            throw new RuntimeException("用户不是商家");
        }
        
        return merchants.get(0).getMerchantId();
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUsrId, Long targetId, String targetType, 
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUsrId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }

    /**
     * 更新店铺信息（名称和描述）
     * 
     * @param merchantId 商家ID
     * @param shopId 店铺ID
     * @param name 店铺名称
     * @param description 店铺描述
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的店铺信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public ShopVO updateShop(Long merchantId, Long shopId, String name, String description, Long operatorUserId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法更新店铺");
        }

        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 验证店铺是否属于该商家
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该店铺");
        }

        // 如果名称发生变化，检查新名称是否已存在（同一商家下，排除当前店铺）
        if (!shop.getName().equals(name)) {
            ShopExample nameExample = new ShopExample();
            nameExample.createCriteria()
                    .andMerchantIdEqualTo(merchantId)
                    .andNameEqualTo(name)
                    .andShopIdNotEqualTo(shopId);
            long existingCount = shopMapper.countByExample(nameExample);
            if (existingCount > 0) {
                throw new RuntimeException("店铺名称已存在，请更换其他名称");
            }
        }

        // 更新店铺信息
        String oldName = shop.getName();
        shop.setName(name);
        shop.setDescription(description);
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.updateByPrimaryKeySelective(shop);

        // 记录审计日志
        recordAuditLog(operatorUserId, shopId, "shop", "update", 
                String.format("商家[%d]更新店铺信息：名称从[%s]改为[%s]", merchantId, oldName, name));

        // 清除相关缓存
        clearShopCache(shopId, merchantId);

        return convertToShopVO(shop);
    }

    /**
     * 获取所有店铺列表（管理员使用，支持筛选和分页）
     * 
     * @param status 店铺状态（可选，null表示查询所有状态）
     * @param keyword 关键词（可选，搜索店铺名称）
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 店铺列表
     */
    public ShopListVO getAllShops(Integer status, String keyword, Integer page, Integer size) {
        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建查询条件
        ShopExample example = new ShopExample();
        ShopExample.Criteria criteria = example.createCriteria();
        
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            criteria.andNameLike("%" + keyword.trim() + "%");
        }
        
        example.setOrderByClause("create_time DESC");

        // 查询总数
        long total = shopMapper.countByExample(example);

        // 查询所有符合条件的店铺（由于店铺数量通常不会太多，使用内存分页）
        List<Shop> allShops = shopMapper.selectByExample(example);
        
        // 内存分页
        List<Shop> pagedShops;
        if (allShops.size() > offset) {
            int endIndex = Math.min(offset + size, allShops.size());
            pagedShops = allShops.subList(offset, endIndex);
        } else {
            pagedShops = new ArrayList<>();
        }

        // 转换为VO
        List<ShopVO> shopVOs = pagedShops.stream()
                .map(this::convertToShopVO)
                .collect(Collectors.toList());

        // 构建返回结果
        ShopListVO result = new ShopListVO();
        result.setShops(shopVOs);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        return result;
    }

    /**
     * 商家重新开启店铺（从 CLOSED 恢复到 ACTIVE）
     * 
     * @param merchantId 商家ID
     * @param shopId 店铺ID
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的店铺信息
     * @throws RuntimeException 操作失败时抛出异常
     */
    @Transactional
    public ShopVO resumeShop(Long merchantId, Long shopId, Long operatorUserId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法开启店铺");
        }

        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 验证店铺是否属于该商家
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该店铺");
        }

        // 检查店铺状态（只有 CLOSED 状态的店铺才能重新开启，ADMIN_CLOSED 需要管理员操作）
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.CLOSED) {
            if (shop.getStatus() == ShopStatus.ADMIN_CLOSED) {
                throw new RuntimeException("店铺已被管理员强制关闭，请联系管理员恢复");
            }
            throw new RuntimeException("只有已关闭的店铺才能重新开启");
        }

        // 更新店铺状态为 ACTIVE
        shop.setStatus(ShopStatus.ACTIVE);
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.updateByPrimaryKeySelective(shop);

        // 记录审计日志
        recordAuditLog(operatorUserId, shopId, "shop", "resume", 
                String.format("商家[%d]重新开启店铺[%s]", merchantId, shop.getName()));

        // 清除相关缓存
        clearShopCache(shopId, merchantId);

        return convertToShopVO(shop);
    }

    /**
     * 管理员更新店铺状态
     * 
     * @param shopId 店铺ID
     * @param status 新状态
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public void updateShopStatus(Long shopId, Integer status, Long operatorUserId) {
        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 验证状态值
        if (status != ShopStatus.ACTIVE && status != ShopStatus.CLOSED && status != ShopStatus.ADMIN_CLOSED) {
            throw new RuntimeException("无效的店铺状态");
        }

        // 如果状态变为CLOSED（商家关闭）或ADMIN_CLOSED（管理员强制关闭），执行关闭店铺的逻辑
        if ((status == ShopStatus.CLOSED || status == ShopStatus.ADMIN_CLOSED) 
                && shop.getStatus() != status) {
            // 1. 更新店铺状态
            shop.setStatus(status);
            shop.setUpdateTime(LocalDateTime.now());
            shopMapper.updateByPrimaryKeySelective(shop);

            // 2. 将该店铺下所有商品状态改为 OFF_SHELF
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andShopIdEqualTo(shopId);
            List<Product> products = productMapper.selectByExample(productExample);
            
            for (Product product : products) {
                product.setStatus(ProductStatus.OFF_SHELF);
                product.setUpdateTime(LocalDateTime.now());
                productMapper.updateByPrimaryKeySelective(product);
            }

            // 3. 将该店铺下所有未支付订单（status = WAIT_PAY）改为 CANCELLED
            OrderExample orderExample = new OrderExample();
            orderExample.createCriteria()
                    .andShopIdEqualTo(shopId)
                    .andStatusEqualTo(OrderStatus.WAIT_PAY);
            List<Order> waitPayOrders = orderMapper.selectByExample(orderExample);
            
            for (Order order : waitPayOrders) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.updateByPrimaryKeySelective(order);
            }

            // 4. 记录审计日志
            String closeType = status == ShopStatus.ADMIN_CLOSED ? "管理员强制关闭" : "商家关闭";
            recordAuditLog(operatorUserId, shopId, "shop", "close", 
                    String.format("%s店铺[%s]", closeType, shop.getName()));

            // 5. 清除相关缓存
            clearShopCache(shopId, shop.getMerchantId());
            return;
        }

        // 如果状态变为ACTIVE（从关闭状态恢复），需要清除相关缓存
        if (status == ShopStatus.ACTIVE && shop.getStatus() != ShopStatus.ACTIVE) {
            // 更新店铺状态
            shop.setStatus(status);
            shop.setUpdateTime(LocalDateTime.now());
            shopMapper.updateByPrimaryKeySelective(shop);

            // 记录审计日志
            recordAuditLog(operatorUserId, shopId, "shop", "update_status",
                    String.format("管理员将店铺[%s]状态修改为[正常营业]", shop.getName()));

            // 清除相关缓存
            clearShopCache(shopId, shop.getMerchantId());
            return;
        }

        // 更新店铺状态（其他情况）
        shop.setStatus(status);
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.updateByPrimaryKeySelective(shop);

        // 记录审计日志
        String statusName = status == ShopStatus.ACTIVE ? "正常营业" : 
                           (status == ShopStatus.ADMIN_CLOSED ? "管理员强制关闭" : "关闭");
        recordAuditLog(operatorUserId, shopId, "shop", "update_status",
                String.format("管理员将店铺[%s]状态修改为[%s]", shop.getName(), statusName));

        // 清除相关缓存
        clearShopCache(shopId, shop.getMerchantId());
    }

    /**
     * 清除店铺相关缓存
     * 
     * @param shopId 店铺ID
     * @param merchantId 商家ID
     */
    private void clearShopCache(Long shopId, Long merchantId) {
        // 清除店铺详情缓存
        String shopDetailKey = SHOP_DETAIL_KEY_PREFIX + shopId;
        redisTemplate.delete(shopDetailKey);

        // 清除商家店铺列表缓存
        String merchantShopsKey = MERCHANT_SHOPS_KEY_PREFIX + merchantId;
        redisTemplate.delete(merchantShopsKey);

        // 清除店铺商品列表缓存（使用通配符）
        String shopProductsKeyPattern = "shop:products:" + shopId + ":*";
        Set<String> keys = redisTemplate.keys(shopProductsKeyPattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 转换为 ShopVO
     */
    private ShopVO convertToShopVO(Shop shop) {
        ShopVO vo = new ShopVO();
        vo.setShopId(shop.getShopId());
        vo.setMerchantId(shop.getMerchantId());
        vo.setName(shop.getName());
        vo.setStatus(shop.getStatus());
        vo.setDescription(shop.getDescription());
        vo.setCreateTime(shop.getCreateTime());
        vo.setUpdateTime(shop.getUpdateTime());
        return vo;
    }
}

