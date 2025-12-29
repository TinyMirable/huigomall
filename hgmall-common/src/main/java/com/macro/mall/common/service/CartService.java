package com.macro.mall.common.service;

import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.common.constant.UserStatus;
import com.macro.mall.common.domain.AddCartItemRequest;
import com.macro.mall.common.domain.CartItemVO;
import com.macro.mall.common.domain.UpdateCartItemRequest;
import com.macro.mall.mapper.CartItemMapper;
import com.macro.mall.mapper.ProductMapper;
import com.macro.mall.mapper.ShopMapper;
import com.macro.mall.mapper.SkuMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务
 */
@Service
public class CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UsrMapper usrMapper;

    /**
     * 获取用户的所有购物车项
     * 
     * @param userId 用户ID
     * @return 购物车项列表
     */
    public List<CartItemVO> getCartItemList(Long userId) {
        CartItemExample example = new CartItemExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("create_time DESC");
        List<CartItem> cartItems = cartItemMapper.selectByExample(example);
        
        return cartItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个购物车项详情
     * 
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @return 购物车项信息，如果不存在或不属于该用户返回null
     */
    public CartItemVO getCartItem(Long userId, Long itemId) {
        CartItemKey key = new CartItemKey();
        key.setUserId(userId);
        key.setItemId(itemId);
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(key);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            return null;
        }
        return convertToVO(cartItem);
    }

    /**
     * 添加购物车项
     * 如果用户购物车中已存在该SKU，则增加数量；否则创建新的购物车项
     * 
     * @param userId 用户ID
     * @param request 添加购物车项请求
     * @return 购物车项信息
     * @throws RuntimeException 添加失败时抛出异常
     */
    @Transactional
    public CartItemVO addCartItem(Long userId, AddCartItemRequest request) {
        // 0. 验证用户状态
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("用户已被禁用，无法添加购物车");
        }

        Long skuId = request.getSkuId();
        Integer num = request.getNum();

        // 验证SKU是否存在
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }

        // 验证商品是否上架
        Product product = productMapper.selectByPrimaryKey(sku.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() == null || product.getStatus() != ProductStatus.ON_SHELF) {
            throw new RuntimeException("商品已下架");
        }

        // 验证店铺是否正常
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法购买");
        }

        // 验证库存
        if (sku.getStock() == null || sku.getStock() < num) {
            throw new RuntimeException("库存不足，当前库存：" + (sku.getStock() == null ? 0 : sku.getStock()));
        }

        // 查询用户购物车中是否已存在该SKU
        CartItemExample example = new CartItemExample();
        example.createCriteria().andUserIdEqualTo(userId).andSkuIdEqualTo(skuId);
        List<CartItem> existingItems = cartItemMapper.selectByExample(example);

        CartItem cartItem;
        if (!existingItems.isEmpty()) {
            // 如果已存在，更新数量
            cartItem = existingItems.get(0);
            int newNum = cartItem.getNum() + num;
            // 再次验证库存（考虑购物车中已有的数量）
            if (sku.getStock() < newNum) {
                throw new RuntimeException("库存不足，当前库存：" + sku.getStock() + "，购物车中已有：" + cartItem.getNum());
            }
            cartItem.setNum(newNum);
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateByPrimaryKeySelective(cartItem);
        } else {
            // 如果不存在，创建新的购物车项
            // 生成item_id：使用当前用户购物车项的最大item_id + 1，如果没有则从1开始
            Long itemId = getNextItemId(userId);
            
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setItemId(itemId);
            cartItem.setSkuId(skuId);
            cartItem.setNum(num);
            cartItem.setRoleCode(null); // 暂时不设置角色编码
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insertSelective(cartItem);
        }

        return convertToVO(cartItem);
    }

    /**
     * 更新购物车项数量
     * 
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @param request 更新购物车项请求
     * @return 更新后的购物车项信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public CartItemVO updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        // 验证购物车项是否存在且属于该用户
        CartItemKey key = new CartItemKey();
        key.setUserId(userId);
        key.setItemId(itemId);
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(key);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该购物车项");
        }

        // 验证SKU库存
        Sku sku = skuMapper.selectByPrimaryKey(cartItem.getSkuId());
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }
        if (sku.getStock() == null || sku.getStock() < request.getNum()) {
            throw new RuntimeException("库存不足，当前库存：" + (sku.getStock() == null ? 0 : sku.getStock()));
        }

        // 更新数量
        cartItem.setNum(request.getNum());
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemMapper.updateByPrimaryKeySelective(cartItem);

        return convertToVO(cartItem);
    }

    /**
     * 删除购物车项
     * 
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @throws RuntimeException 删除失败时抛出异常
     */
    @Transactional
    public void deleteCartItem(Long userId, Long itemId) {
        // 验证购物车项是否存在且属于该用户
        CartItemKey key = new CartItemKey();
        key.setUserId(userId);
        key.setItemId(itemId);
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(key);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该购物车项");
        }

        // 删除购物车项
        cartItemMapper.deleteByPrimaryKey(key);
    }

    /**
     * 获取用户购物车中下一个可用的item_id
     * 
     * @param userId 用户ID
     * @return 下一个item_id
     */
    private Long getNextItemId(Long userId) {
        CartItemExample example = new CartItemExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("item_id DESC");
        List<CartItem> items = cartItemMapper.selectByExample(example);
        
        if (items.isEmpty()) {
            return 1L;
        } else {
            // 找到最大的item_id
            Long maxItemId = items.stream()
                    .map(CartItem::getItemId)
                    .max(Long::compareTo)
                    .orElse(0L);
            return maxItemId + 1;
        }
    }

    /**
     * 将CartItem实体转换为CartItemVO
     * 包含关联的SKU、商品、店铺信息
     * 
     * @param cartItem 购物车项实体
     * @return 购物车项VO
     */
    private CartItemVO convertToVO(CartItem cartItem) {
        CartItemVO vo = new CartItemVO();
        vo.setUserId(cartItem.getUserId());
        vo.setItemId(cartItem.getItemId());
        vo.setSkuId(cartItem.getSkuId());
        vo.setNum(cartItem.getNum());
        vo.setRoleCode(cartItem.getRoleCode());
        vo.setCreateTime(cartItem.getCreateTime());
        vo.setUpdateTime(cartItem.getUpdateTime());

        // 查询SKU信息
        Sku sku = skuMapper.selectByPrimaryKey(cartItem.getSkuId());
        if (sku != null) {
            vo.setSpec(sku.getSpec());
            vo.setPrice(sku.getPrice());
            vo.setStock(sku.getStock());
            vo.setSkuImageUrl(sku.getImageUrl());

            // 查询商品信息
            Product product = productMapper.selectByPrimaryKey(sku.getProductId());
            if (product != null) {
                vo.setProductId(product.getProductId());
                vo.setProductName(product.getName());
                vo.setProductImageUrl(product.getImageUrl());
                vo.setProductStatus(product.getStatus());

                // 查询店铺信息
                Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
                if (shop != null) {
                    vo.setShopId(shop.getShopId());
                    vo.setShopName(shop.getName());
                    vo.setShopStatus(shop.getStatus());
                }
            }
        }

        // 计算小计金额
        if (vo.getPrice() != null && vo.getNum() != null) {
            vo.setSubtotal(vo.getPrice().multiply(BigDecimal.valueOf(vo.getNum())));
        }

        // 判断是否可用：商品上架、店铺正常营业、库存充足
        boolean available = (vo.getProductStatus() != null && vo.getProductStatus() == com.macro.mall.common.constant.ProductStatus.ON_SHELF)
                && (vo.getShopStatus() != null && vo.getShopStatus() == com.macro.mall.common.constant.ShopStatus.ACTIVE)
                && (vo.getStock() != null && vo.getStock() >= vo.getNum());
        vo.setAvailable(available);

        return vo;
    }
}

