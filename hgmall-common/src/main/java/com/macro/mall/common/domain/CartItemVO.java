package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项展示VO
 * 包含购物车项信息以及关联的SKU、商品、店铺信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 购物车项ID
     */
    private Long itemId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // ========== SKU信息 ==========
    /**
     * SKU规格（JSON格式）
     */
    private String spec;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * SKU库存
     */
    private Integer stock;

    /**
     * SKU图片URL
     */
    private String skuImageUrl;

    // ========== 商品信息 ==========
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图URL
     */
    private String productImageUrl;

    /**
     * 商品状态：1-上架，0-下架
     */
    private Integer productStatus;

    // ========== 店铺信息 ==========
    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺状态：1-正常，0-禁用
     */
    private Integer shopStatus;

    // ========== 计算字段 ==========
    /**
     * 小计金额（价格 * 数量）
     */
    private BigDecimal subtotal;

    /**
     * 是否可用（商品上架且店铺正常且库存充足）
     */
    private Boolean available;
}




