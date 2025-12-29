package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 商家表
 *
 * merchant
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Merchant {
    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 用户ID（注册商家）
     */
    private Long userId;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

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
     * 获取 商家名称
     *
     * @return 商家名称
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * 设置 商家名称
     *
     * @param merchantName 商家名称
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName == null ? null : merchantName.trim();
    }

    /**
     * 获取 负责人
     *
     * @return 负责人
     */
    public String getOwner() {
        return owner;
    }

    /**
     * 设置 负责人
     *
     * @param owner 负责人
     */
    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    /**
     * 获取 用户ID（注册商家）
     *
     * @return 用户ID（注册商家）
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置 用户ID（注册商家）
     *
     * @param userId 用户ID（注册商家）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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