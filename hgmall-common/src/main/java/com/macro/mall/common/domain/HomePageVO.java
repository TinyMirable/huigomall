package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageVO {
    /**
     * 分类商品列表（每个一级分类下最新16个商品）
     */
    private List<CategoryProductsVO> categoryProducts;

    /**
     * 全站销量Top3商品
     */
    private List<ProductVO> topSalesProducts;
}












