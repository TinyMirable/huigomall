package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类商品列表VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProductsVO {
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品列表
     */
    private List<ProductVO> products;
}












