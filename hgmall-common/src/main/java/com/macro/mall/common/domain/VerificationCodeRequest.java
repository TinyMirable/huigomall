package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 验证码请求DTO
 */
@Data
public class VerificationCodeRequest {

    /**
     * 手机号或邮箱
     * 手机号格式：11位数字
     * 邮箱格式：标准邮箱格式
     */
    @NotBlank(message = "手机号或邮箱不能为空")
    @Pattern(regexp = "^(1[3-9]\\d{9}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", 
             message = "请输入正确的手机号或邮箱格式")
    private String contact;
}

