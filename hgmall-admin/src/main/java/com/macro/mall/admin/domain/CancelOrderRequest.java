package com.macro.mall.admin.domain;

/**
 * 取消订单请求
 */
public class CancelOrderRequest {
    private String reason; // 必填，取消原因

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}



