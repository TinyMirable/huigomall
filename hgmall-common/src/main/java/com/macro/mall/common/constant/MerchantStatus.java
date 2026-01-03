package com.macro.mall.common.constant;

/**
 * 商家状态常量
 * 统一的状态码系统
 */
public class MerchantStatus {
    /**
     * ACTIVE：正常（1）
     * 商家可以正常管理店铺、商品、订单等
     */
    public static final int ACTIVE = 1;

    /**
     * DISABLED：禁用（0）
     * 商家被禁用，无法管理店铺、商品、订单等操作
     */
    public static final int DISABLED = 0;

    private MerchantStatus() {
        // 工具类，禁止实例化
    }
}











