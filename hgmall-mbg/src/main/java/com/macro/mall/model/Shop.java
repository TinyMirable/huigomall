package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 店铺表
 *
 * shop
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Shop {
    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

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
     * 获取 商家ID
     *
     * @return 商家ID
     */
    public Long getMerchantId() {
        return merchantId;
    }

    /**
     * 设置 商家ID
     *
     * @param merchantId 商家ID
     */
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 获取 店铺名称
     *
     * @return 店铺名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 店铺名称
     *
     * @param name 店铺名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 状态：1-正常，0-禁用
     *
     * @return 状态：1-正常，0-禁用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 状态：1-正常，0-禁用
     *
     * @param status 状态：1-正常，0-禁用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取 店铺描述
     *
     * @return 店铺描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 店铺描述
     *
     * @param description 店铺描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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