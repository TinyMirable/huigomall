package com.macro.mall.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表（小订单，按店铺分组）
 *
 * order
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Order {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 批量订单ID（关联大订单）
     */
    private Long batchOrderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     */
    private Integer status;

    /**
     * 订单总金额
     */
    private BigDecimal total;

    /**
     * 收货地址ID（订单创建时的地址快照）
     */
    private Long addressId;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

    /**
     * 获取 订单ID
     *
     * @return 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置 订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取 批量订单ID（关联大订单）
     *
     * @return 批量订单ID（关联大订单）
     */
    public Long getBatchOrderId() {
        return batchOrderId;
    }

    /**
     * 设置 批量订单ID（关联大订单）
     *
     * @param batchOrderId 批量订单ID（关联大订单）
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
     * 获取 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     *
     * @return 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     *
     * @param status 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取 订单总金额
     *
     * @return 订单总金额
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * 设置 订单总金额
     *
     * @param total 订单总金额
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
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