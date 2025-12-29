package com.macro.mall.model;

/**
 * 商品分类关联表
 *
 * product_category
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class ProductCategoryKey {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 获取 商品ID
     *
     * @return 商品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置 商品ID
     *
     * @param productId 商品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 获取 分类ID
     *
     * @return 分类ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置 分类ID
     *
     * @param categoryId 分类ID
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}