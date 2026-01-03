package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 忘记密码请求DTO
 */
@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "邮箱不能为空")
    private String email;
    
    @NotBlank(message = "验证码不能为空")
    private String code;
    
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}














