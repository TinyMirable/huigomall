package com.macro.mall.model;

/**
 * 订单项表
 *
 * order_item
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class OrderItemKey {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单项ID
     */
    private Long itemId;

    /**
     * 获取 订单ID
     *
     * @return 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置 订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取 订单项ID
     *
     * @return 订单项ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * 设置 订单项ID
     *
     * @param itemId 订单项ID
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}