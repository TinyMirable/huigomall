package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单项ID
     */
    private Long itemId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU规格（JSON格式）
     */
    private String spec;

    /**
     * 快照价格（下单时的价格）
     */
    private BigDecimal priceSnapshot;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片URL
     */
    private String productImageUrl;

    /**
     * SKU图片URL
     */
    private String skuImageUrl;

    /**
     * 小计金额（价格 * 数量）
     */
    private BigDecimal subtotal;
}





