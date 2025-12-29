package com.macro.mall.common.domain;

import java.util.List;

/**
 * 店铺商品列表VO
 */
public class ShopProductListVO {
    private List<ShopProductVO> products;
    private Long total;
    private Integer page;
    private Integer size;

    public List<ShopProductVO> getProducts() {
        return products;
    }

    public void setProducts(List<ShopProductVO> products) {
        this.products = products;
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





