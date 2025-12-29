package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO（小订单）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 批量订单ID
     */
    private Long batchOrderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款
     */
    private Integer status;

    /**
     * 订单总金额
     */
    private BigDecimal total;

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
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> orderItems;
}




