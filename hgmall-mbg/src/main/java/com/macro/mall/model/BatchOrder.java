package com.macro.mall.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 批量订单表（大订单）
 *
 * batch_order
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class BatchOrder {
    /**
     * 批量订单ID
     */
    private Long batchOrderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总金额（所有小订单金额之和）
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 收货地址ID（订单创建时的地址快照）
     */
    private Long addressId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 过期时间（用于倒计时，默认30分钟后过期）
     */
    private LocalDateTime expireTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 获取 批量订单ID
     *
     * @return 批量订单ID
     */
    public Long getBatchOrderId() {
        return batchOrderId;
    }

    /**
     * 设置 批量订单ID
     *
     * @param batchOrderId 批量订单ID
     */
    public void setBatchOrderId(Long batchOrderId) {
        this.batchOrderId = batchOrderId;
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
     * 获取 总金额（所有小订单金额之和）
     *
     * @return 总金额（所有小订单金额之和）
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置 总金额（所有小订单金额之和）
     *
     * @param totalAmount 总金额（所有小订单金额之和）
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     *
     * @return 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     *
     * @param status 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取 收货地址ID（订单创建时的地址快照）
     *
     * @return 收货地址ID（订单创建时的地址快照）
     */
    public Long getAddressId() {
        return addressId;
    }

    /**
     * 设置 收货地址ID（订单创建时的地址快照）
     *
     * @param addressId 收货地址ID（订单创建时的地址快照）
     */
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    /**
     * 获取 创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 支付时间
     *
     * @return 支付时间
     */
    public LocalDateTime getPayTime() {
        return payTime;
    }

    /**
     * 设置 支付时间
     *
     * @param payTime 支付时间
     */
    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取 过期时间（用于倒计时，默认30分钟后过期）
     *
     * @return 过期时间（用于倒计时，默认30分钟后过期）
     */
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    /**
     * 设置 过期时间（用于倒计时，默认30分钟后过期）
     *
     * @param expireTime 过期时间（用于倒计时，默认30分钟后过期）
     */
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取 更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置 更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}