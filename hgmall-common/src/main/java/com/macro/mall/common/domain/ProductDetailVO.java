package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品详情VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailVO {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * SKU列表
     */
    private List<SkuVO> skus;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 店铺ID
     */
    private Long shopId;
}

