package com.macro.mall.portal.service;

import com.macro.mall.common.constant.OrderStatus;
import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.common.constant.UserStatus;
import com.macro.mall.common.domain.*;
import com.macro.mall.common.service.EmailService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Service
public class OrderService {

    @Autowired
    private BatchOrderMapper batchOrderMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key前缀
     */
    private static final String BATCH_ORDER_CACHE_KEY_PREFIX = "batch_order:detail:";
    
    /**
     * 订单过期时间（分钟）
     */
    private static final int ORDER_EXPIRE_MINUTES = 30;

    /**
     * 创建订单（从购物车）
     * 
     * @param userId 用户ID
     * @param request 创建订单请求
     * @return 批量订单信息
     * @throws RuntimeException 创建失败时抛出异常
     */
    @Transactional
    public BatchOrderVO createOrder(Long userId, CreateOrderRequest request) {
        // 0. 验证用户状态
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("用户已被禁用，无法创建订单");
        }

        // 1. 获取购物车项
        List<CartItem> cartItems = getCartItems(userId, request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }

        // 2. 验证地址
        Address address = addressMapper.selectByPrimaryKey(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在或无权访问");
        }

        // 3. 按店铺分组购物车项
        Map<Long, List<CartItem>> shopCartItemsMap = cartItems.stream()
                .collect(Collectors.groupingBy(item -> {
                    Sku sku = skuMapper.selectByPrimaryKey(item.getSkuId());
                    if (sku == null) {
                        throw new RuntimeException("SKU不存在: " + item.getSkuId());
                    }
                    Product product = productMapper.selectByPrimaryKey(sku.getProductId());
                    if (product == null) {
                        throw new RuntimeException("商品不存在");
                    }
                    return product.getShopId();
                }));

        // 4. 创建批量订单
        BatchOrder batchOrder = new BatchOrder();
        batchOrder.setUserId(userId);
        batchOrder.setAddressId(request.getAddressId());
        batchOrder.setStatus(OrderStatus.WAIT_PAY); // 待支付
        batchOrder.setCreateTime(LocalDateTime.now());
        batchOrder.setUpdateTime(LocalDateTime.now());
        batchOrder.setExpireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));

        // 5. 创建小订单和订单项，计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Order> orders = new ArrayList<>();
        // 存储每个订单对应的购物车项，用于后续创建订单项
        Map<Long, List<CartItem>> orderCartItemsMap = new HashMap<>();

        for (Map.Entry<Long, List<CartItem>> entry : shopCartItemsMap.entrySet()) {
            Long shopId = entry.getKey();
            List<CartItem> shopCartItems = entry.getValue();

            // 验证店铺
            Shop shop = shopMapper.selectByPrimaryKey(shopId);
            if (shop == null || shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
                throw new RuntimeException("店铺不存在或非正常营业状态: " + shopId);
            }

            // 创建小订单
            Order order = new Order();
            order.setUserId(userId);
            order.setShopId(shopId);
            order.setAddressId(request.getAddressId());
            order.setStatus(OrderStatus.WAIT_PAY); // 待支付
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());

            BigDecimal orderTotal = BigDecimal.ZERO;

            // 验证并扣减库存，计算订单金额
            for (CartItem cartItem : shopCartItems) {
                Sku sku = skuMapper.selectByPrimaryKey(cartItem.getSkuId());
                if (sku == null) {
                    throw new RuntimeException("SKU不存在: " + cartItem.getSkuId());
                }

                // 验证库存
                if (sku.getStock() == null || sku.getStock() < cartItem.getNum()) {
                    throw new RuntimeException("库存不足: SKU " + cartItem.getSkuId() + 
                            "，当前库存: " + (sku.getStock() == null ? 0 : sku.getStock()) + 
                            "，需要: " + cartItem.getNum());
                }

                // 验证商品状态
                Product product = productMapper.selectByPrimaryKey(sku.getProductId());
                if (product == null || product.getStatus() == null || product.getStatus() != ProductStatus.ON_SHELF) {
                    throw new RuntimeException("商品不存在或已下架: " + sku.getProductId());
                }

                // 扣减库存（原子操作）
                // 先再次查询最新库存
                Sku currentSku = skuMapper.selectByPrimaryKey(sku.getSkuId());
                if (currentSku.getStock() < cartItem.getNum()) {
                    throw new RuntimeException("库存不足: SKU " + cartItem.getSkuId() + 
                            "，当前库存: " + currentSku.getStock() + "，需要: " + cartItem.getNum());
                }
                currentSku.setStock(currentSku.getStock() - cartItem.getNum());
                skuMapper.updateByPrimaryKeySelective(currentSku);

                // 计算订单金额
                BigDecimal itemTotal = sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getNum()));
                orderTotal = orderTotal.add(itemTotal);
            }

            order.setTotal(orderTotal);
            totalAmount = totalAmount.add(orderTotal);
            orders.add(order);
            // 保存订单和购物车项的映射关系
            orderCartItemsMap.put(shopId, shopCartItems);
        }

        // 设置批量订单总金额
        batchOrder.setTotalAmount(totalAmount);

        // 6. 保存批量订单
        batchOrderMapper.insertSelective(batchOrder);

        // 7. 保存小订单
        for (Order order : orders) {
            order.setBatchOrderId(batchOrder.getBatchOrderId());
            orderMapper.insertSelective(order);
        }

        // 8. 保存订单项
        for (Order order : orders) {
            List<CartItem> shopCartItems = orderCartItemsMap.get(order.getShopId());
            if (shopCartItems == null) {
                continue;
            }
            
            Long itemId = 1L;
            for (CartItem cartItem : shopCartItems) {
                Sku sku = skuMapper.selectByPrimaryKey(cartItem.getSkuId());
                if (sku == null) {
                    continue;
                }
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getOrderId());
                orderItem.setItemId(itemId++);
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setPriceSnapshot(sku.getPrice());
                orderItem.setNum(cartItem.getNum());
                orderItemMapper.insertSelective(orderItem);
            }
        }

        // 9. 删除购物车项
        for (CartItem cartItem : cartItems) {
            CartItemKey key = new CartItemKey();
            key.setUserId(userId);
            key.setItemId(cartItem.getItemId());
            cartItemMapper.deleteByPrimaryKey(key);
        }

        // 10. 缓存订单信息到Redis
        cacheBatchOrder(batchOrder.getBatchOrderId());

        // 11. 转换为VO并返回
        return convertToBatchOrderVO(batchOrder);
    }

    /**
     * 获取购物车项
     */
    private List<CartItem> getCartItems(Long userId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            // 获取所有购物车项
            CartItemExample example = new CartItemExample();
            example.createCriteria().andUserIdEqualTo(userId);
            return cartItemMapper.selectByExample(example);
        } else {
            // 获取指定的购物车项
            CartItemExample example = new CartItemExample();
            example.createCriteria().andUserIdEqualTo(userId)
                    .andItemIdIn(cartItemIds);
            return cartItemMapper.selectByExample(example);
        }
    }

    /**
     * 获取用户的批量订单列表
     * 
     * @param userId 用户ID
     * @return 批量订单列表
     */
    public List<BatchOrderVO> getBatchOrderList(Long userId) {
        BatchOrderExample example = new BatchOrderExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("create_time DESC");
        List<BatchOrder> batchOrders = batchOrderMapper.selectByExample(example);
        
        return batchOrders.stream()
                .map(this::convertToBatchOrderVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取批量订单详情
     * 
     * @param batchOrderId 批量订单ID
     * @param userId 用户ID（用于验证权限）
     * @return 批量订单详情
     */
    public BatchOrderVO getBatchOrderDetail(Long batchOrderId, Long userId) {
        // 先尝试从缓存获取
        String cacheKey = BATCH_ORDER_CACHE_KEY_PREFIX + batchOrderId;
        BatchOrderVO cached = (BatchOrderVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            // 验证用户权限
            if (!cached.getUserId().equals(userId)) {
                throw new RuntimeException("无权访问该订单");
            }
            return cached;
        }

        // 从数据库查询
        BatchOrder batchOrder = batchOrderMapper.selectByPrimaryKey(batchOrderId);
        if (batchOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!batchOrder.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该订单");
        }

        BatchOrderVO vo = convertToBatchOrderVO(batchOrder);
        
        // 缓存到Redis
        cacheBatchOrder(batchOrderId);
        
        return vo;
    }

    /**
     * 支付订单
     * 
     * @param batchOrderId 批量订单ID
     * @param userId 用户ID
     * @return 支付后的订单信息
     * @throws RuntimeException 支付失败时抛出异常
     */
    @Transactional
    public BatchOrderVO payOrder(Long batchOrderId, Long userId) {
        // 0. 验证用户状态
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("用户已被禁用，无法支付订单");
        }

        // 1. 验证订单
        BatchOrder batchOrder = batchOrderMapper.selectByPrimaryKey(batchOrderId);
        if (batchOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!batchOrder.getUserId().equals(userId)) {
            throw new RuntimeException("无权支付该订单");
        }
        if (batchOrder.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        if (LocalDateTime.now().isAfter(batchOrder.getExpireTime())) {
            throw new RuntimeException("订单已过期");
        }

        // 2. 更新批量订单状态
        batchOrder.setStatus(OrderStatus.PAID); // 已支付
        batchOrder.setPayTime(LocalDateTime.now());
        batchOrder.setUpdateTime(LocalDateTime.now());
        batchOrderMapper.updateByPrimaryKeySelective(batchOrder);

        // 3. 更新所有小订单状态
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andBatchOrderIdEqualTo(batchOrderId);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        
        for (Order order : orders) {
            order.setStatus(OrderStatus.PAID); // 已支付
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // 4. 发送支付成功邮件通知（每个小订单一封邮件）
        Usr userForEmail = usrMapper.selectByPrimaryKey(userId);
        if (userForEmail != null && userForEmail.getEmail() != null && !userForEmail.getEmail().isEmpty()) {
            for (Order order : orders) {
                sendPaymentSuccessEmail(userForEmail.getEmail(), order);
            }
        }

        // 5. 更新缓存
        cacheBatchOrder(batchOrderId);

        return convertToBatchOrderVO(batchOrder);
    }

    /**
     * 取消订单
     * 
     * @param batchOrderId 批量订单ID
     * @param userId 用户ID
     * @throws RuntimeException 取消失败时抛出异常
     */
    @Transactional
    public void cancelOrder(Long batchOrderId, Long userId) {
        // 1. 验证订单
        BatchOrder batchOrder = batchOrderMapper.selectByPrimaryKey(batchOrderId);
        if (batchOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!batchOrder.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消该订单");
        }
        if (batchOrder.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确，无法取消");
        }

        // 2. 更新批量订单状态
        batchOrder.setStatus(2); // 已取消
        batchOrder.setUpdateTime(LocalDateTime.now());
        batchOrderMapper.updateByPrimaryKeySelective(batchOrder);

        // 3. 更新所有小订单状态
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andBatchOrderIdEqualTo(batchOrderId);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        
        for (Order order : orders) {
            order.setStatus(4); // 已取消
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateByPrimaryKeySelective(order);

            // 恢复库存
            restoreStock(order.getOrderId());
            
            // 恢复购物车项
            restoreCartItems(userId, order.getOrderId());
        }

        // 4. 删除缓存
        String cacheKey = BATCH_ORDER_CACHE_KEY_PREFIX + batchOrderId;
        redisTemplate.delete(cacheKey);
    }
    
    /**
     * 恢复订单商品到购物车
     */
    private void restoreCartItems(Long userId, Long orderId) {
        // 查询订单项
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        
        for (OrderItem orderItem : orderItems) {
            // 检查购物车中是否已存在该SKU
            CartItemExample cartExample = new CartItemExample();
            cartExample.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andSkuIdEqualTo(orderItem.getSkuId());
            List<CartItem> existingItems = cartItemMapper.selectByExample(cartExample);
            
            if (!existingItems.isEmpty()) {
                // 如果已存在，增加数量
                CartItem existingItem = existingItems.get(0);
                existingItem.setNum(existingItem.getNum() + orderItem.getNum());
                CartItemKey key = new CartItemKey();
                key.setUserId(userId);
                key.setItemId(existingItem.getItemId());
                cartItemMapper.updateByPrimaryKeySelective(existingItem);
            } else {
                // 如果不存在，创建新的购物车项
                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setSkuId(orderItem.getSkuId());
                cartItem.setNum(orderItem.getNum());
                
                // 获取下一个可用的itemId
                Long nextItemId = getNextCartItemId(userId);
                cartItem.setItemId(nextItemId);
                
                cartItemMapper.insertSelective(cartItem);
            }
        }
    }
    
    /**
     * 获取用户购物车中下一个可用的item_id
     */
    private Long getNextCartItemId(Long userId) {
        CartItemExample example = new CartItemExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("item_id DESC");
        List<CartItem> items = cartItemMapper.selectByExample(example);
        
        if (items.isEmpty()) {
            return 1L;
        } else {
            Long maxItemId = items.stream()
                    .map(CartItem::getItemId)
                    .max(Long::compareTo)
                    .orElse(0L);
            return maxItemId + 1;
        }
    }

    /**
     * 恢复订单库存
     */
    private void restoreStock(Long orderId) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        
        for (OrderItem orderItem : orderItems) {
            Sku sku = skuMapper.selectByPrimaryKey(orderItem.getSkuId());
            if (sku != null) {
                sku.setStock((sku.getStock() == null ? 0 : sku.getStock()) + orderItem.getNum());
                skuMapper.updateByPrimaryKeySelective(sku);
            }
        }
    }

    /**
     * 发送支付成功邮件
     */
    private void sendPaymentSuccessEmail(String email, Order order) {
        try {
            // 获取订单项
            OrderItemExample example = new OrderItemExample();
            example.createCriteria().andOrderIdEqualTo(order.getOrderId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
            
            // 获取店铺信息
            Shop shop = shopMapper.selectByPrimaryKey(order.getShopId());
            String shopName = shop != null ? shop.getName() : "未知店铺";
            
            // 构建邮件内容
            StringBuilder content = new StringBuilder();
            content.append("您的订单已支付成功！\n\n");
            content.append("订单号: ").append(order.getOrderId()).append("\n");
            content.append("店铺: ").append(shopName).append("\n");
            content.append("订单金额: ¥").append(order.getTotal()).append("\n");
            content.append("订单状态: 已支付\n\n");
            content.append("商品列表:\n");
            
            for (OrderItem item : orderItems) {
                Sku sku = skuMapper.selectByPrimaryKey(item.getSkuId());
                if (sku != null) {
                    Product product = productMapper.selectByPrimaryKey(sku.getProductId());
                    if (product != null) {
                        content.append("- ").append(product.getName())
                                .append(" (规格: ").append(sku.getSpec() != null ? sku.getSpec() : "默认").append(")")
                                .append(" x").append(item.getNum())
                                .append(" ¥").append(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getNum())))
                                .append("\n");
                    }
                }
            }
            
            content.append("\n商家正在为您准备商品，发货后我们会及时通知您。\n");
            content.append("感谢您的购买！");

            // 发送邮件
            emailService.sendOrderEmail(email, "订单支付成功", content.toString());
        } catch (Exception e) {
            System.err.println("发送支付成功邮件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送发货邮件
     */
    public void sendShippedEmail(Order order) {
        try {
            // 获取用户信息
            Usr user = usrMapper.selectByPrimaryKey(order.getUserId());
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                return; // 用户不存在或没有邮箱，不发送邮件
            }

            // 获取订单项
            OrderItemExample example = new OrderItemExample();
            example.createCriteria().andOrderIdEqualTo(order.getOrderId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
            
            // 获取店铺信息
            Shop shop = shopMapper.selectByPrimaryKey(order.getShopId());
            String shopName = shop != null ? shop.getName() : "未知店铺";
            
            // 构建邮件内容
            StringBuilder content = new StringBuilder();
            content.append("您的商品已发货！\n\n");
            content.append("订单号: ").append(order.getOrderId()).append("\n");
            content.append("店铺: ").append(shopName).append("\n");
            content.append("订单金额: ¥").append(order.getTotal()).append("\n");
            content.append("订单状态: 已发货\n\n");
            content.append("商品列表:\n");
            
            for (OrderItem item : orderItems) {
                Sku sku = skuMapper.selectByPrimaryKey(item.getSkuId());
                if (sku != null) {
                    Product product = productMapper.selectByPrimaryKey(sku.getProductId());
                    if (product != null) {
                        content.append("- ").append(product.getName())
                                .append(" (规格: ").append(sku.getSpec() != null ? sku.getSpec() : "默认").append(")")
                                .append(" x").append(item.getNum())
                                .append(" ¥").append(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getNum())))
                                .append("\n");
                    }
                }
            }
            
            content.append("\n商品已发货，正在配送中，请注意查收！\n");
            content.append("感谢您的购买！");

            // 发送邮件
            emailService.sendOrderEmail(user.getEmail(), "商品已发货", content.toString());
        } catch (Exception e) {
            System.err.println("发送发货邮件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 缓存批量订单到Redis
     */
    private void cacheBatchOrder(Long batchOrderId) {
        BatchOrder batchOrder = batchOrderMapper.selectByPrimaryKey(batchOrderId);
        if (batchOrder != null) {
            BatchOrderVO vo = convertToBatchOrderVO(batchOrder);
            String cacheKey = BATCH_ORDER_CACHE_KEY_PREFIX + batchOrderId;
            // 缓存30分钟
            redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.MINUTES);
        }
    }

    /**
     * 转换为BatchOrderVO
     */
    private BatchOrderVO convertToBatchOrderVO(BatchOrder batchOrder) {
        BatchOrderVO vo = new BatchOrderVO();
        vo.setBatchOrderId(batchOrder.getBatchOrderId());
        vo.setUserId(batchOrder.getUserId());
        vo.setTotalAmount(batchOrder.getTotalAmount());
        vo.setStatus(batchOrder.getStatus());
        vo.setAddressId(batchOrder.getAddressId());
        vo.setCreateTime(batchOrder.getCreateTime());
        vo.setPayTime(batchOrder.getPayTime());
        vo.setExpireTime(batchOrder.getExpireTime());
        vo.setUpdateTime(batchOrder.getUpdateTime());

        // 设置地址信息
        if (batchOrder.getAddressId() != null) {
            Address address = addressMapper.selectByPrimaryKey(batchOrder.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                addressVO.setAddressId(address.getAddressId());
                addressVO.setUserId(address.getUserId());
                addressVO.setDetail(address.getDetail());
                addressVO.setReceiverName(address.getReceiverName());
                addressVO.setReceiverPhone(address.getReceiverPhone());
                addressVO.setIsDefault(address.getIsDefault());
                addressVO.setCreateTime(address.getCreateTime());
                addressVO.setUpdateTime(address.getUpdateTime());
                vo.setAddress(addressVO);
            }
        }

        // 设置小订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andBatchOrderIdEqualTo(batchOrder.getBatchOrderId());
        List<Order> orders = orderMapper.selectByExample(orderExample);
        vo.setOrders(orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList()));

        // 计算剩余支付时间
        if (batchOrder.getStatus() == 0 && batchOrder.getExpireTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(batchOrder.getExpireTime())) {
                Duration duration = Duration.between(now, batchOrder.getExpireTime());
                vo.setRemainingSeconds(duration.getSeconds());
            } else {
                vo.setRemainingSeconds(0L);
            }
        }

        return vo;
    }

    /**
     * 转换为OrderVO
     */
    private OrderVO convertToOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setBatchOrderId(order.getBatchOrderId());
        vo.setUserId(order.getUserId());
        vo.setShopId(order.getShopId());
        vo.setStatus(order.getStatus());
        vo.setTotal(order.getTotal());
        vo.setAddressId(order.getAddressId());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        // 设置店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(order.getShopId());
        if (shop != null) {
            vo.setShopName(shop.getName());
        }

        // 设置地址信息
        if (order.getAddressId() != null) {
            Address address = addressMapper.selectByPrimaryKey(order.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                addressVO.setAddressId(address.getAddressId());
                addressVO.setUserId(address.getUserId());
                addressVO.setDetail(address.getDetail());
                addressVO.setReceiverName(address.getReceiverName());
                addressVO.setReceiverPhone(address.getReceiverPhone());
                addressVO.setIsDefault(address.getIsDefault());
                addressVO.setCreateTime(address.getCreateTime());
                addressVO.setUpdateTime(address.getUpdateTime());
                vo.setAddress(addressVO);
            }
        }

        // 设置订单项列表
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(order.getOrderId());
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        vo.setOrderItems(orderItems.stream()
                .map(item -> convertToOrderItemVO(item))
                .collect(Collectors.toList()));

        return vo;
    }

    /**
     * 转换为OrderItemVO
     */
    private OrderItemVO convertToOrderItemVO(OrderItem orderItem) {
        OrderItemVO vo = new OrderItemVO();
        vo.setOrderId(orderItem.getOrderId());
        vo.setItemId(orderItem.getItemId());
        vo.setSkuId(orderItem.getSkuId());
        vo.setPriceSnapshot(orderItem.getPriceSnapshot());
        vo.setNum(orderItem.getNum());

        // 设置SKU和商品信息
        Sku sku = skuMapper.selectByPrimaryKey(orderItem.getSkuId());
        if (sku != null) {
            vo.setSpec(sku.getSpec());
            vo.setSkuImageUrl(sku.getImageUrl());

            Product product = productMapper.selectByPrimaryKey(sku.getProductId());
            if (product != null) {
                vo.setProductId(product.getProductId());
                vo.setProductName(product.getName());
                vo.setProductImageUrl(product.getImageUrl());
            }
        }

        // 计算小计
        if (vo.getPriceSnapshot() != null && vo.getNum() != null) {
            vo.setSubtotal(vo.getPriceSnapshot().multiply(BigDecimal.valueOf(vo.getNum())));
        }

        return vo;
    }
}

