package com.macro.mall.admin.service;

import com.macro.mall.admin.domain.AuditLogListVO;
import com.macro.mall.admin.domain.AuditLogVO;
import com.macro.mall.mapper.AuditLogMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.AuditLog;
import com.macro.mall.model.AuditLogExample;
import com.macro.mall.model.Usr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员审计日志服务
 */
@Service
public class AdminAuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private UsrMapper usrMapper;

    /**
     * 获取审计日志列表（分页、筛选）
     */
    public AuditLogListVO getAuditLogList(String targetType, String operationType,
                                          Long adminUserId, Long targetId,
                                          LocalDateTime startTime, LocalDateTime endTime,
                                          Integer page, Integer size) {
        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建查询条件
        AuditLogExample example = new AuditLogExample();
        AuditLogExample.Criteria criteria = example.createCriteria();

        if (targetType != null && !targetType.trim().isEmpty()) {
            criteria.andTargetTypeEqualTo(targetType);
        }

        if (operationType != null && !operationType.trim().isEmpty()) {
            criteria.andOperationTypeEqualTo(operationType);
        }

        if (adminUserId != null) {
            criteria.andAdminUsrIdEqualTo(adminUserId);
        }

        if (targetId != null) {
            criteria.andTargetIdEqualTo(targetId);
        }

        if (startTime != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startTime);
        }

        if (endTime != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endTime);
        }

        // 查询总数
        long total = auditLogMapper.countByExample(example);

        // 查询列表
        example.setOrderByClause("create_time DESC");
        List<AuditLog> auditLogs = auditLogMapper.selectByExample(example);

        // 限制分页
        List<AuditLog> pagedLogs = auditLogs.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        // 转换为VO
        List<AuditLogVO> logVOs = new ArrayList<>();
        for (AuditLog log : pagedLogs) {
            logVOs.add(convertToAuditLogVO(log));
        }

        AuditLogListVO result = new AuditLogListVO();
        result.setAuditLogs(logVOs);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        return result;
    }

    /**
     * 获取审计日志详情
     */
    public AuditLogVO getAuditLogDetail(Long auditId) {
        AuditLog auditLog = auditLogMapper.selectByPrimaryKey(auditId);
        if (auditLog == null) {
            throw new RuntimeException("审计日志不存在");
        }
        return convertToAuditLogVO(auditLog);
    }

    /**
     * 转换为AuditLogVO
     */
    private AuditLogVO convertToAuditLogVO(AuditLog log) {
        AuditLogVO vo = new AuditLogVO();
        vo.setAuditId(log.getAuditId());
        vo.setAdminUserId(log.getAdminUsrId());
        vo.setTargetId(log.getTargetId());
        vo.setTargetType(log.getTargetType());
        vo.setOperationType(log.getOperationType());
        vo.setOperationDesc(log.getOperationDesc());
        vo.setCreateTime(log.getCreateTime());

        // 查询管理员用户名
        if (log.getAdminUsrId() != null) {
            Usr admin = usrMapper.selectByPrimaryKey(log.getAdminUsrId());
            if (admin != null) {
                vo.setAdminUserName(admin.getUserName());
            }
        }

        return vo;
    }
}






