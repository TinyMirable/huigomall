package com.macro.mall.admin.domain;

import java.util.List;

/**
 * 审计日志列表VO
 */
public class AuditLogListVO {
    private List<AuditLogVO> auditLogs;
    private Long total;
    private Integer page;
    private Integer size;

    public List<AuditLogVO> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLogVO> auditLogs) {
        this.auditLogs = auditLogs;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}











