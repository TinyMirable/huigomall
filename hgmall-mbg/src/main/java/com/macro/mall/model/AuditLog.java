package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 审计日志表
 *
 * audit_log
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class AuditLog {
    /**
     * 审计日志ID
     */
    private Long auditId;

    /**
     * 管理员用户ID
     */
    private Long adminUsrId;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 目标对象类型（merchant/shop/product等）
     */
    private String targetType;

    /**
     * 操作类型（create/update/delete等）
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * 获取 审计日志ID
     *
     * @return 审计日志ID
     */
    public Long getAuditId() {
        return auditId;
    }

    /**
     * 设置 审计日志ID
     *
     * @param auditId 审计日志ID
     */
    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    /**
     * 获取 管理员用户ID
     *
     * @return 管理员用户ID
     */
    public Long getAdminUsrId() {
        return adminUsrId;
    }

    /**
     * 设置 管理员用户ID
     *
     * @param adminUsrId 管理员用户ID
     */
    public void setAdminUsrId(Long adminUsrId) {
        this.adminUsrId = adminUsrId;
    }

    /**
     * 获取 目标对象ID
     *
     * @return 目标对象ID
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * 设置 目标对象ID
     *
     * @param targetId 目标对象ID
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * 获取 目标对象类型（merchant/shop/product等）
     *
     * @return 目标对象类型（merchant/shop/product等）
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置 目标对象类型（merchant/shop/product等）
     *
     * @param targetType 目标对象类型（merchant/shop/product等）
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType == null ? null : targetType.trim();
    }

    /**
     * 获取 操作类型（create/update/delete等）
     *
     * @return 操作类型（create/update/delete等）
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 设置 操作类型（create/update/delete等）
     *
     * @param operationType 操作类型（create/update/delete等）
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType == null ? null : operationType.trim();
    }

    /**
     * 获取 操作描述
     *
     * @return 操作描述
     */
    public String getOperationDesc() {
        return operationDesc;
    }

    /**
     * 设置 操作描述
     *
     * @param operationDesc 操作描述
     */
    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc == null ? null : operationDesc.trim();
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
}