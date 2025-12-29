package com.macro.mall.common.domain;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新SKU库存请求
 * 支持增加或减少库存（原子操作）
 * 也可以直接设置为0（标记为无货）
 */
public class UpdateSkuStockRequest {
    /**
     * 库存变化量（正数表示增加，负数表示减少）
     * 如果为null，则使用absoluteStock
     */
    private Integer delta;

    /**
     * 绝对库存值（如果提供，则直接设置为该值，通常用于标记为无货即设为0）
     * 如果为null，则使用delta进行增减
     */
    private Integer absoluteStock;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public Integer getAbsoluteStock() {
        return absoluteStock;
    }

    public void setAbsoluteStock(Integer absoluteStock) {
        this.absoluteStock = absoluteStock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

