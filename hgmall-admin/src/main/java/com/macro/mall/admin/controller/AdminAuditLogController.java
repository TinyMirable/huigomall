package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.AuditLogListVO;
import com.macro.mall.admin.domain.AuditLogVO;
import com.macro.mall.admin.service.AdminAuditLogService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员审计日志控制器
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    @Autowired
    private AdminAuditLogService adminAuditLogService;

    /**
     * 查看审计日志列表
     * GET /api/admin/audit-logs
     */
    @GetMapping
    public CommonResult<AuditLogListVO> getAuditLogList(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) Long adminUserId,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            AuditLogListVO result = adminAuditLogService.getAuditLogList(
                    targetType, operationType, adminUserId, targetId, startTime, endTime, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取审计日志列表失败: " + e.getMessage());
        }
    }

    /**
     * 查看审计日志详情
     * GET /api/admin/audit-logs/{auditId}
     */
    @GetMapping("/{auditId}")
    public CommonResult<AuditLogVO> getAuditLogDetail(
            @PathVariable Long auditId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            AuditLogVO auditLog = adminAuditLogService.getAuditLogDetail(auditId);
            return CommonResult.success("获取成功", auditLog);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取审计日志详情失败: " + e.getMessage());
        }
    }
}



