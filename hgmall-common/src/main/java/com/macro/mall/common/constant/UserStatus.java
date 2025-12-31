package com.macro.mall.common.constant;

/**
 * 用户状态常量
 * 统一的状态码系统
 */
public class UserStatus {
    /**
     * ACTIVE：正常（1）
     * 用户可以正常使用系统所有功能
     */
    public static final int ACTIVE = 1;

    /**
     * DISABLED：禁用（0）
     * 用户被禁用，无法登录、创建订单、下单等操作
     */
    public static final int DISABLED = 0;

    private UserStatus() {
        // 工具类，禁止实例化
    }
}






