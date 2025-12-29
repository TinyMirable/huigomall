package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家注册请求DTO
 */
@Data
public class MerchantRegisterRequest {
    /**
     * 商家名称
     */
    @NotBlank(message = "商家名称不能为空")
    @Size(min = 2, max = 100, message = "商家名称长度必须在2-100个字符之间")
    private String merchantName;
    
    /**
     * 负责人姓名（可选）
     */
    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String owner;
}
