package com.macro.mall.common.constant;

/**
 * 订单状态常量
 */
public class OrderStatus {
    /**
     * WAIT_PAY：待支付
     */
    public static final int WAIT_PAY = 0;

    /**
     * PAID：已支付
     */
    public static final int PAID = 1;

    /**
     * SHIPPED：已发货
     */
    public static final int SHIPPED = 2;

    /**
     * COMPLETED：已完成
     */
    public static final int COMPLETED = 3;

    /**
     * CANCELLED：已取消
     */
    public static final int CANCELLED = 4;

    /**
     * REFUNDING：退款中
     */
    public static final int REFUNDING = 5;

    /**
     * REFUNDED：已退款
     */
    public static final int REFUNDED = 6;

    private OrderStatus() {
        // 工具类，禁止实例化
    }
}












