package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量订单VO（大订单）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchOrderVO {
    /**
     * 批量订单ID
     */
    private Long batchOrderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总金额（所有小订单金额之和）
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
     */
    private Integer status;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 收货地址信息（快照）
     */
    private AddressVO address;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 小订单列表
     */
    private List<OrderVO> orders;

    /**
     * 剩余支付时间（秒），用于倒计时
     */
    private Long remainingSeconds;
}







