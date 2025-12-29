package com.macro.mall.common.constant;

/**
 * 店铺状态常量
 */
public class ShopStatus {
    /**
     * ACTIVE：正常营业（1）
     */
    public static final int ACTIVE = 1;

    /**
     * CLOSED：商家主动关闭（2）
     */
    public static final int CLOSED = 2;

    /**
     * ADMIN_CLOSED：管理员强制关闭（3）
     */
    public static final int ADMIN_CLOSED = 3;

    private ShopStatus() {
        // 工具类，禁止实例化
    }
}



