package com.macro.mall.common.domain;

import java.util.List;

/**
 * 店铺列表VO（分页）
 */
public class ShopListVO {
    /**
     * 店铺列表
     */
    private List<ShopVO> shops;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    public List<ShopVO> getShops() {
        return shops;
    }

    public void setShops(List<ShopVO> shops) {
        this.shops = shops;
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

