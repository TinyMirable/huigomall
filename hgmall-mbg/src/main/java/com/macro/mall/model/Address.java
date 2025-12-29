package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 地址表
 *
 * address
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Address {
    /**
     * 地址ID
     */
    private Long addressId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

    /**
     * 获取 地址ID
     *
     * @return 地址ID
     */
    public Long getAddressId() {
        return addressId;
    }

    /**
     * 设置 地址ID
     *
     * @param addressId 地址ID
     */
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     * 获取 用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置 用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取 详细地址
     *
     * @return 详细地址
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 设置 详细地址
     *
     * @param detail 详细地址
     */
    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    /**
     * 获取 收货人姓名
     *
     * @return 收货人姓名
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * 设置 收货人姓名
     *
     * @param receiverName 收货人姓名
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    /**
     * 获取 收货人电话
     *
     * @return 收货人电话
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * 设置 收货人电话
     *
     * @param receiverPhone 收货人电话
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    /**
     * 获取 是否默认地址
     *
     * @return 是否默认地址
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * 设置 是否默认地址
     *
     * @param isDefault 是否默认地址
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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