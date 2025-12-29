package com.macro.mall.model;

/**
 * 购物车表
 *
 * cart_item
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class CartItemKey {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 购物车项ID
     */
    private Long itemId;

    /**
     * 获取 用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置 用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取 购物车项ID
     *
     * @return 购物车项ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * 设置 购物车项ID
     *
     * @param itemId 购物车项ID
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}