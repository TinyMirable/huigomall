package com.macro.mall.common.domain;

/**
 * 发货请求
 */
public class ShipOrderRequest {
    private String remark; // 可选，用于记录备注

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}



