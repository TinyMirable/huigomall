package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.HomePageVO;
import com.macro.mall.common.domain.ProductDetailVO;
import com.macro.mall.common.domain.ProductListVO;
import com.macro.mall.common.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 首页接口
     * GET /api/home
     * 返回每个一级分类下最新16个商品 + 全站销量Top3商品
     */
    @GetMapping("/home")
    public CommonResult<HomePageVO> getHomePage() {
        try {
            HomePageVO homePage = productService.getHomePage();
            return CommonResult.success("获取成功", homePage);
        } catch (Exception e) {
            return CommonResult.failed("获取首页数据失败: " + e.getMessage());
        }
    }

    /**
     * 分类商品列表接口
     * GET /api/products
     * 参数：
     *   - categories（可选，支持多个分类ID，如：?categories=1&categories=2 或 ?categories=1,2,3）
     *   - category（可选，单个分类ID，向后兼容，如：?category=1）
     *   - page（可选，默认1）
     *   - size（可选，默认20）
     * 
     * 支持方式：
     *   1. 单个分类：?category=1
     *   2. 多个分类：?categories=1&categories=2 或 ?categories=1,2,3
     *   3. 全部商品：不传分类参数
     */
    @GetMapping("/products")
    public CommonResult<ProductListVO> getProductList(
            @RequestParam(required = false) java.util.List<Long> categories,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            // 处理向后兼容：如果只传了 category，转换为 categories 列表
            if (categories == null || categories.isEmpty()) {
                if (category != null) {
                    categories = java.util.Collections.singletonList(category);
                }
            }
            
            ProductListVO productList = productService.getProductList(categories, page, size);
            return CommonResult.success("获取成功", productList);
        } catch (Exception e) {
            return CommonResult.failed("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 商品详情接口
     * GET /api/products/{productId}
     */
    @GetMapping("/products/{productId}")
    public CommonResult<ProductDetailVO> getProductDetail(@PathVariable Long productId) {
        try {
            ProductDetailVO productDetail = productService.getProductDetail(productId);
            if (productDetail == null) {
                return CommonResult.failed("商品不存在或已下架");
            }
            return CommonResult.success("获取成功", productDetail);
        } catch (Exception e) {
            return CommonResult.failed("获取商品详情失败: " + e.getMessage());
        }
    }
}

