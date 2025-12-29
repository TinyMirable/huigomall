package com.macro.mall.common.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新购物车项请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {
    /**
     * 数量（必填，至少为1）
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer num;
}




