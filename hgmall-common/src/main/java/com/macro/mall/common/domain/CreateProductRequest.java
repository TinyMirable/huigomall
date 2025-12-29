package com.macro.mall.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 创建商品请求
 */
public class CreateProductRequest {
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String name;

    @Size(max = 2000, message = "商品描述长度不能超过2000个字符")
    private String description;

    private String imageUrl;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    @NotNull(message = "SKU列表不能为空")
    @Size(min = 1, message = "至少需要添加一个SKU")
    @Valid
    private List<CreateSkuRequest> skus;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

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

    public List<CreateSkuRequest> getSkus() {
        return skus;
    }

    public void setSkus(List<CreateSkuRequest> skus) {
        this.skus = skus;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

