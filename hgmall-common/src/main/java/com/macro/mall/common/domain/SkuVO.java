package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SKU VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuVO {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 规格（JSON格式）
     */
    private String spec;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * SKU图片URL
     */
    private String imageUrl;
}




