package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付订单请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequest {
    /**
     * 批量订单ID（必填）
     */
    @NotNull(message = "批量订单ID不能为空")
    private Long batchOrderId;

    /**
     * 支付方式（可选，现阶段不实现实际支付，仅用于标识）
     */
    private String paymentMethod;
}












