package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建订单请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    /**
     * 购物车项ID列表（可选，如果为空则使用用户所有购物车项）
     */
    private List<Long> cartItemIds;

    /**
     * 收货地址ID（必填）
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
}




