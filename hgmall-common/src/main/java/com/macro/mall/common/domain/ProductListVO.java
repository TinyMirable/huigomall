package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品列表VO（分页）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListVO {
    /**
     * 商品列表
     */
    private List<ProductVO> products;

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
}





