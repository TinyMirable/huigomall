package com.macro.mall.common.domain;

import java.util.List;

/**
 * 商家订单列表VO
 */
public class MerchantOrderListVO {
    private List<MerchantOrderVO> orders;
    private Long total;
    private Integer page;
    private Integer size;

    public List<MerchantOrderVO> getOrders() {
        return orders;
    }

    public void setOrders(List<MerchantOrderVO> orders) {
        this.orders = orders;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}



