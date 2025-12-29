package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新店铺请求
 */
public class UpdateShopRequest {
    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 2, max = 100, message = "店铺名称长度必须在2-100个字符之间")
    private String name;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

