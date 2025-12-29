package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 重新开启店铺请求
 */
public class ResumeShopRequest {
    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

