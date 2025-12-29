package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 店铺商品详情VO（用于编辑）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductDetailVO {
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
     * 商品主图URL
     */
    private String imageUrl;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * SKU列表
     */
    private List<SkuVO> skus;
}




