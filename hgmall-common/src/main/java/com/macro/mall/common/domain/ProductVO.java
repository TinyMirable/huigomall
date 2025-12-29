package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品展示VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 最低SKU价格
     */
    private BigDecimal minPrice;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 店铺ID
     */
    private Long shopId;
}

