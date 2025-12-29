package com.macro.mall.admin.domain;

import java.util.List;

/**
 * 商家列表VO
 */
public class MerchantListVO {
    private List<MerchantVO> merchants;
    private Long total;
    private Integer page;
    private Integer size;

    public List<MerchantVO> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MerchantVO> merchants) {
        this.merchants = merchants;
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



