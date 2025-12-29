package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 * 支持用户名/邮箱/手机号+密码登录
 * identifier字段可以是用户名、邮箱或手机号
 */
@Data
public class LoginRequest {
    /**
     * 标识符：可以是用户名、邮箱或手机号
     */
    @NotBlank(message = "用户名/邮箱/手机号不能为空")
    private String identifier;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}







