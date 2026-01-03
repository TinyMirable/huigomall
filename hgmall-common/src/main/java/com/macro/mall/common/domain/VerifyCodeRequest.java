package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 验证码验证请求（用于敏感操作）
 */
public class VerifyCodeRequest {
    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}












