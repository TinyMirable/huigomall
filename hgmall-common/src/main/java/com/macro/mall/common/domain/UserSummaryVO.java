package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息概要VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号（脱敏）
     */
    private String phoneNumber;

    /**
     * 角色代码（如：USER, ADMIN）
     */
    private String role;

    /**
     * 商家状态（NONE: 无商家, PENDING: 待审核, APPROVED: 已通过, REJECTED: 已拒绝）
     */
    private String merchantStatus;

    /**
     * 商家ID（如果用户是商家）
     */
    private Long merchantId;

    /**
     * 店铺数量
     */
    private Integer shopCount;

    /**
     * 订单数量
     */
    private Integer orderCount;
}


