package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.CategoryVO;
import com.macro.mall.common.service.CategoryService;
import com.macro.mall.mapper.CategoryMapper;
import com.macro.mall.model.Category;
import com.macro.mall.model.CategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key
     */
    private static final String CATEGORIES_KEY = "categories:top";

    /**
     * 缓存TTL（秒）
     */
    private static final long CATEGORIES_TTL = 600; // 10分钟（分类数据变化不频繁）

    /**
     * 获取所有一级分类列表
     * GET /api/categories
     * 
     * @return 分类列表
     */
    @GetMapping
    public CommonResult<List<Category>> getCategories() {
        try {
            // 尝试从缓存获取
            @SuppressWarnings("unchecked")
            List<Category> cached = (List<Category>) redisTemplate.opsForValue().get(CATEGORIES_KEY);
            if (cached != null) {
                return CommonResult.success("获取成功", cached);
            }

            // 查询数据库
            CategoryExample example = new CategoryExample();
            example.createCriteria().andParentIdIsNull();
            example.setOrderByClause("sort_order ASC, category_id ASC");
            List<Category> categories = categoryMapper.selectByExample(example);

            // 存入缓存（随机TTL，10-15分钟）
            long ttl = CATEGORIES_TTL + new Random().nextInt(300);
            redisTemplate.opsForValue().set(CATEGORIES_KEY, categories, ttl, TimeUnit.SECONDS);

            return CommonResult.success("获取成功", categories);
        } catch (Exception e) {
            return CommonResult.failed("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取分类树（限制深度为3）
     * GET /api/categories/tree
     * 
     * @return 分类树
     */
    @GetMapping("/tree")
    public CommonResult<List<CategoryVO>> getCategoryTree() {
        try {
            List<CategoryVO> tree = categoryService.getCategoryTree();
            return CommonResult.success("获取成功", tree);
        } catch (Exception e) {
            return CommonResult.failed("获取分类树失败: " + e.getMessage());
        }
    }
}


