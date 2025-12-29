package com.macro.mall.model;

import java.time.LocalDateTime;

/**
 * 分类表
 *
 * category
 *
 * @author MyBatis Generator
 * @date 2025-12-27
 */
public class Category {
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

    /**
     * 获取 分类ID
     *
     * @return 分类ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置 分类ID
     *
     * @param categoryId 分类ID
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取 父分类ID
     *
     * @return 父分类ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置 父分类ID
     *
     * @param parentId 父分类ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取 分类名称
     *
     * @return 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 分类名称
     *
     * @param name 分类名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 排序序号
     *
     * @return 排序序号
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置 排序序号
     *
     * @param sortOrder 排序序号
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 获取 create_time
     *
     * @return create_time
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置 create_time
     *
     * @param createTime create_time
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 update_time
     *
     * @return update_time
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置 update_time
     *
     * @param updateTime update_time
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}