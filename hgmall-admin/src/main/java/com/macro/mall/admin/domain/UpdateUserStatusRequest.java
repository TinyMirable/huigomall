package com.macro.mall.admin.domain;

/**
 * 更新用户状态请求
 */
public class UpdateUserStatusRequest {
    private Integer status; // 1-正常，0-禁用
    private String reason; // 可选，操作原因

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}






