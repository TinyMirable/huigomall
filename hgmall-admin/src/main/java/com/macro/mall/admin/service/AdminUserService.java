package com.macro.mall.admin.service;

import com.macro.mall.admin.domain.UserListVO;
import com.macro.mall.admin.domain.UserVO;
import com.macro.mall.common.domain.MerchantOrderListVO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户管理服务
 */
@Service
public class AdminUserService {

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 获取用户列表（分页、筛选）
     */
    public UserListVO getUserList(Long roleId, Integer status, String keyword, Integer page, Integer size) {
        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建查询条件
        UsrExample example = new UsrExample();
        UsrExample.Criteria criteria = example.createCriteria();

        if (roleId != null) {
            criteria.andRoleIdEqualTo(roleId);
        }

        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordPattern = "%" + keyword.trim() + "%";
            example.or().andUserNameLike(keywordPattern);
            example.or().andEmailLike(keywordPattern);
        }

        // 查询总数
        long total = usrMapper.countByExample(example);

        // 查询列表
        example.setOrderByClause("create_time DESC");
        List<Usr> users = usrMapper.selectByExample(example);

        // 限制分页
        List<Usr> pagedUsers = users.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        // 转换为VO
        List<UserVO> userVOs = new ArrayList<>();
        for (Usr user : pagedUsers) {
            userVOs.add(convertToUserVO(user));
        }

        UserListVO result = new UserListVO();
        result.setUsers(userVOs);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        return result;
    }

    /**
     * 获取用户详情
     */
    public UserVO getUserDetail(Long userId) {
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToUserVO(user);
    }

    /**
     * 管理用户状态
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status, String reason, Long adminUserId) {
        if (status != 0 && status != 1) {
            throw new RuntimeException("无效的用户状态");
        }

        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 记录审计日志
        String statusText = status == 1 ? "启用" : "禁用";
        recordAuditLog(adminUserId, userId, "user", "update_status",
                String.format("管理员%s用户[%s]。原因：%s", statusText, user.getUserName(), reason != null ? reason : "无"));
    }

    /**
     * 获取用户订单历史
     */
    public MerchantOrderListVO getUserOrders(Long userId, Integer status, Integer page, Integer size) {
        // 这里需要查询该用户的所有订单
        // 由于MerchantOrderService是查询商家订单的，我们需要创建一个新的查询方法
        // 暂时返回空列表，后续可以扩展
        MerchantOrderListVO result = new MerchantOrderListVO();
        result.setOrders(new ArrayList<>());
        result.setTotal(0L);
        result.setPage(page != null ? page : 1);
        result.setSize(size != null ? size : 20);
        return result;
    }

    /**
     * 转换为UserVO
     * 管理员接口返回完整信息，不进行脱敏
     */
    private UserVO convertToUserVO(Usr user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        // 管理员可以看到完整信息，不进行脱敏
        vo.setUserName(user.getUserName());
        vo.setEmail(user.getEmail());
        vo.setPhoneNumber(user.getPhoneNumber());
        vo.setRoleId(user.getRoleId());
        vo.setStatus(user.getStatus());
        vo.setStatusText(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role != null) {
            vo.setRoleCode(role.getRoleCode());
            vo.setRoleName(role.getRoleName());
        }

        // 查询商家信息
        MerchantExample merchantExample = new MerchantExample();
        merchantExample.createCriteria().andUserIdEqualTo(user.getUserId());
        List<Merchant> merchants = merchantMapper.selectByExample(merchantExample);
        if (!merchants.isEmpty()) {
            Merchant merchant = merchants.get(0);
            vo.setMerchantId(merchant.getMerchantId());
            vo.setMerchantName(merchant.getMerchantName());
        }

        // 查询订单数量
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andUserIdEqualTo(user.getUserId());
        long orderCount = orderMapper.countByExample(orderExample);
        vo.setOrderCount((int) orderCount);

        return vo;
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUserId, Long targetId, String targetType,
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUserId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }
}

