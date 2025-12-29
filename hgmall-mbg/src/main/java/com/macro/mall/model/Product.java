package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 商品表
 *
 * product
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Product {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 状态：1-上架，0-下架，2-管理员下架
     */
    private Integer status;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品主图URL
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
     * 获取 店铺ID
     *
     * @return 店铺ID
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * 设置 店铺ID
     *
     * @param shopId 店铺ID
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * 获取 商品名称
     *
     * @return 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 商品名称
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 状态：1-上架，0-下架
     *
     * @return 状态：1-上架，0-下架
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 状态：1-上架，0-下架
     *
     * @param status 状态：1-上架，0-下架
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取 商品描述
     *
     * @return 商品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 商品描述
     *
     * @param description 商品描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 商品主图URL
     *
     * @return 商品主图URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置 商品主图URL
     *
     * @param imageUrl 商品主图URL
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