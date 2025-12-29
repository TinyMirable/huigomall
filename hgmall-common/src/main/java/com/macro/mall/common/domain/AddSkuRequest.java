package com.macro.mall.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 添加SKU请求
 * 用于向已有商品添加新的SKU
 */
public class AddSkuRequest {
    @Valid
    @NotNull(message = "SKU列表不能为空")
    @Size(min = 1, message = "至少需要添加一个SKU")
    private List<CreateSkuRequest> skus;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public List<CreateSkuRequest> getSkus() {
        return skus;
    }

    public void setSkus(List<CreateSkuRequest> skus) {
        this.skus = skus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

