package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新SKU图片请求
 */
public class UpdateSkuImageRequest {
    @NotBlank(message = "SKU图片URL不能为空")
    private String imageUrl;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}



