package com.macro.mall.model;

import java.math.BigDecimal;

/**
 * 订单项表
 *
 * order_item
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class OrderItem extends OrderItemKey {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 快照价格（下单时的价格）
     */
    private BigDecimal priceSnapshot;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 获取 SKU ID
     *
     * @return SKU ID
     */
    public Long getSkuId() {
        return skuId;
    }

    /**
     * 设置 SKU ID
     *
     * @param skuId SKU ID
     */
    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    /**
     * 获取 快照价格（下单时的价格）
     *
     * @return 快照价格（下单时的价格）
     */
    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    /**
     * 设置 快照价格（下单时的价格）
     *
     * @param priceSnapshot 快照价格（下单时的价格）
     */
    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    /**
     * 获取 数量
     *
     * @return 数量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置 数量
     *
     * @param num 数量
     */
    public void setNum(Integer num) {
        this.num = num;
    }
}