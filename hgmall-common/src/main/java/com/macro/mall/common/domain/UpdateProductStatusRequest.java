package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新商品状态请求（需要邮箱验证码）
 */
public class UpdateProductStatusRequest {
    @NotNull(message = "商品状态不能为空")
    private Integer status;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}












