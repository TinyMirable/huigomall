package com.macro.mall.admin.domain;

/**
 * 更新分类请求
 * 优先级会自动根据分类级别生成，不再需要手动指定
 */
public class UpdateCategoryRequest {
    private String name;
    private Long parentId; // 父分类ID，如果修改了父分类，优先级会自动重新计算

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}

