package com.macro.mall.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU表（库存量单位）
 *
 * sku
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Sku {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long productId;

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

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

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
     * 获取 规格（JSON格式）
     *
     * @return 规格（JSON格式）
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 设置 规格（JSON格式）
     *
     * @param spec 规格（JSON格式）
     */
    public void setSpec(String spec) {
        this.spec = spec == null ? null : spec.trim();
    }

    /**
     * 获取 价格
     *
     * @return 价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置 价格
     *
     * @param price 价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取 库存
     *
     * @return 库存
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 设置 库存
     *
     * @param stock 库存
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * 获取 SKU图片URL
     *
     * @return SKU图片URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置 SKU图片URL
     *
     * @param imageUrl SKU图片URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    /**
     * 获取 create_time
     *
     * @return create_time
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置 create_time
     *
     * @param createTime create_time
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 update_time
     *
     * @return update_time
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置 update_time
     *
     * @param updateTime update_time
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}