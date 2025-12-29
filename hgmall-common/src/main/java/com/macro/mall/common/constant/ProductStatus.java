package com.macro.mall.common.constant;

/**
 * 商品状态常量
 * 统一的状态码系统
 */
public class ProductStatus {
    /**
     * ON_SHELF：上架（1）
     * 商品正常在售，用户可以购买
     */
    public static final int ON_SHELF = 1;

    /**
     * OFF_SHELF：下架（0）
     * 商家主动下架，用户无法购买
     */
    public static final int OFF_SHELF = 0;

    /**
     * ADMIN_OFF_SHELF：管理员下架（2）
     * 管理员强制下架，商家无法自行上架，需要管理员操作
     */
    public static final int ADMIN_OFF_SHELF = 2;

    private ProductStatus() {
        // 工具类，禁止实例化
    }
}


