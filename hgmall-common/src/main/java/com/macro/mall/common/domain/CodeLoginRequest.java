package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码登录请求DTO
 */
@Data
public class CodeLoginRequest {
    @NotBlank(message = "联系方式不能为空")
    private String contact; // 邮箱或手机号
    
    @NotBlank(message = "验证码不能为空")
    private String code;
}









