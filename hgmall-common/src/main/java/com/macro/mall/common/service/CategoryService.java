package com.macro.mall.common.service;

import com.macro.mall.common.domain.CategoryVO;
import com.macro.mall.mapper.CategoryMapper;
import com.macro.mall.model.Category;
import com.macro.mall.model.CategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分类服务（公共模块）
 * 提供分类树查询等功能，供管理员和用户使用
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String CATEGORY_TREE_CACHE_KEY = "category:tree";
    private static final long CATEGORY_TREE_CACHE_TTL = 3600; // 1小时

    /**
     * 最大分类树深度
     */
    private static final int MAX_DEPTH = 3;

    /**
     * 获取分类树（限制深度为3）
     * 
     * @return 分类树
     */
    public List<CategoryVO> getCategoryTree() {
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<CategoryVO> cached = (List<CategoryVO>) redisTemplate.opsForValue().get(CATEGORY_TREE_CACHE_KEY);
        if (cached != null) {
            return cached;
        }

        // 查询所有分类
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort_order ASC, category_id ASC");
        List<Category> categories = categoryMapper.selectByExample(example);

        // 构建分类树（限制深度为3）
        List<CategoryVO> tree = buildCategoryTree(categories, MAX_DEPTH);

        // 存入缓存
        redisTemplate.opsForValue().set(CATEGORY_TREE_CACHE_KEY, tree, CATEGORY_TREE_CACHE_TTL, TimeUnit.SECONDS);

        return tree;
    }

    /**
     * 构建分类树（限制深度）
     * 
     * @param categories 所有分类列表
     * @param maxDepth 最大深度
     * @return 分类树
     */
    private List<CategoryVO> buildCategoryTree(List<Category> categories, int maxDepth) {
        // 转换为VO
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());

        // 构建父子关系（限制深度）
        List<CategoryVO> rootCategories = new ArrayList<>();
        Map<Long, CategoryVO> categoryMap = categoryVOs.stream()
                .collect(Collectors.toMap(CategoryVO::getCategoryId, vo -> vo));

        for (CategoryVO vo : categoryVOs) {
            if (vo.getParentId() == null) {
                // 根分类
                rootCategories.add(vo);
            } else {
                // 子分类，找到父分类
                CategoryVO parent = categoryMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }

        // 限制深度：递归删除超过最大深度的子分类
        for (CategoryVO root : rootCategories) {
            limitDepth(root, 1, maxDepth);
        }

        return rootCategories;
    }

    /**
     * 限制分类树深度
     * 
     * @param category 当前分类
     * @param currentDepth 当前深度
     * @param maxDepth 最大深度
     */
    private void limitDepth(CategoryVO category, int currentDepth, int maxDepth) {
        if (category.getChildren() == null || category.getChildren().isEmpty()) {
            return;
        }

        if (currentDepth >= maxDepth) {
            // 达到最大深度，清空子分类
            category.setChildren(null);
            return;
        }

        // 递归处理子分类
        for (CategoryVO child : category.getChildren()) {
            limitDepth(child, currentDepth + 1, maxDepth);
        }
    }

    /**
     * 递归获取分类及其所有子分类的ID列表
     * 
     * @param categoryId 分类ID
     * @return 分类ID列表（包含自身和所有子分类）
     */
    public List<Long> getCategoryIdsWithChildren(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }

        Set<Long> categoryIds = new HashSet<>();
        categoryIds.add(categoryId);
        
        // 递归获取所有子分类
        getChildrenCategoryIds(categoryId, categoryIds);
        
        return new ArrayList<>(categoryIds);
    }

    /**
     * 递归获取子分类ID
     * 
     * @param parentId 父分类ID
     * @param categoryIds 分类ID集合（用于收集结果）
     */
    private void getChildrenCategoryIds(Long parentId, Set<Long> categoryIds) {
        CategoryExample example = new CategoryExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        List<Category> children = categoryMapper.selectByExample(example);
        
        for (Category child : children) {
            categoryIds.add(child.getCategoryId());
            // 递归获取子分类的子分类
            getChildrenCategoryIds(child.getCategoryId(), categoryIds);
        }
    }

    /**
     * 批量获取分类及其所有子分类的ID列表
     * 
     * @param categoryIds 分类ID列表
     * @return 分类ID列表（包含自身和所有子分类）
     */
    public List<Long> getCategoryIdsWithChildren(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> allCategoryIds = new HashSet<>();
        for (Long categoryId : categoryIds) {
            allCategoryIds.add(categoryId);
            getChildrenCategoryIds(categoryId, allCategoryIds);
        }
        
        return new ArrayList<>(allCategoryIds);
    }

    /**
     * 转换为CategoryVO
     */
    private CategoryVO convertToCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setCategoryId(category.getCategoryId());
        vo.setParentId(category.getParentId());
        vo.setName(category.getName());
        vo.setSortOrder(category.getSortOrder());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }

    /**
     * 清除分类树缓存
     */
    public void clearCategoryTreeCache() {
        redisTemplate.delete(CATEGORY_TREE_CACHE_KEY);
    }
}



