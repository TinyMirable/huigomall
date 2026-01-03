package com.macro.mall.admin.domain;

/**
 * 审核商家请求
 */
public class AuditMerchantRequest {
    private String action; // approve-同意，reject-拒绝
    private String reason; // 可选，审核意见

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}











