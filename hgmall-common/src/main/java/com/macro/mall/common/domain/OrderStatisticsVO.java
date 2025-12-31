package com.macro.mall.common.domain;

import java.math.BigDecimal;

/**
 * 订单统计VO
 */
public class OrderStatisticsVO {
    private Long totalOrders; // 总订单数
    private Long paidOrders; // 已支付订单数
    private Long shippedOrders; // 已发货订单数
    private Long completedOrders; // 已完成订单数
    private Long cancelledOrders; // 已取消订单数
    private BigDecimal totalAmount; // 总金额
    private BigDecimal paidAmount; // 已支付金额
    private BigDecimal completedAmount; // 已完成金额

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getPaidOrders() {
        return paidOrders;
    }

    public void setPaidOrders(Long paidOrders) {
        this.paidOrders = paidOrders;
    }

    public Long getShippedOrders() {
        return shippedOrders;
    }

    public void setShippedOrders(Long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getCompletedAmount() {
        return completedAmount;
    }

    public void setCompletedAmount(BigDecimal completedAmount) {
        this.completedAmount = completedAmount;
    }
}






