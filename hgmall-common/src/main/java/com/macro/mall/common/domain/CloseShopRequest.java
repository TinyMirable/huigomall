package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 关闭店铺请求（需要邮箱验证码）
 */
public class CloseShopRequest {
    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}












