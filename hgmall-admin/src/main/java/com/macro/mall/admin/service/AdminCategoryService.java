package com.macro.mall.admin.service;

import com.macro.mall.admin.domain.CategoryVO;
import com.macro.mall.common.service.CategoryService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员分类管理服务
 */
@Service
public class AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private CategoryService categoryService;

    /**
     * 创建分类
     * 优先级自动根据分类级别生成：父分类是1，子分类是1+1
     */
    @Transactional
    public CategoryVO createCategory(Long parentId, String name, Integer sortOrder, Long adminUserId) {
        // 验证父分类
        Category parent = null;
        if (parentId != null && parentId != 0) {
            parent = categoryMapper.selectByPrimaryKey(parentId);
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
        }

        // 自动计算优先级：父分类是1，子分类是父分类优先级+1
        Integer priority;
        if (parent == null) {
            // 一级分类，优先级为1
            priority = 1;
        } else {
            // 子分类，优先级为父分类优先级+1
            Integer parentPriority = parent.getSortOrder();
            if (parentPriority == null) {
                parentPriority = 1; // 如果父分类没有优先级，默认为1
            }
            priority = parentPriority + 1;
        }

        Category category = new Category();
        category.setParentId(parentId != null && parentId == 0 ? null : parentId);
        category.setName(name);
        category.setSortOrder(priority); // 使用自动计算的优先级
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insertSelective(category);

        // 清除分类树缓存
        categoryService.clearCategoryTreeCache();

        // 记录审计日志
        recordAuditLog(adminUserId, category.getCategoryId(), "category", "create",
                String.format("管理员创建分类[%s]", name));

        return convertToCategoryVO(category);
    }

    /**
     * 修改分类
     * 如果修改了父分类，优先级会自动重新计算
     */
    @Transactional
    public CategoryVO updateCategory(Long categoryId, String name, Long newParentId, Long adminUserId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        if (name != null) {
            category.setName(name);
        }
        
        // 如果修改了父分类，需要重新计算优先级
        if (newParentId != null) {
            Long oldParentId = category.getParentId();
            if (oldParentId == null && newParentId == 0) {
                // 都是根分类，不需要修改
                newParentId = null;
            } else if (oldParentId != null && oldParentId.equals(newParentId)) {
                // 父分类没变，不需要修改
                newParentId = null;
            } else {
                // 父分类变了，需要重新计算优先级
                Category newParent = null;
                if (newParentId != null && newParentId != 0) {
                    newParent = categoryMapper.selectByPrimaryKey(newParentId);
                    if (newParent == null) {
                        throw new RuntimeException("父分类不存在");
                    }
                }
                
                Integer priority;
                if (newParent == null) {
                    // 一级分类，优先级为1
                    priority = 1;
                } else {
                    // 子分类，优先级为父分类优先级+1
                    Integer parentPriority = newParent.getSortOrder();
                    if (parentPriority == null) {
                        parentPriority = 1;
                    }
                    priority = parentPriority + 1;
                }
                category.setSortOrder(priority);
                category.setParentId(newParentId == 0 ? null : newParentId);
            }
        }
        
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateByPrimaryKeySelective(category);

        // 清除分类树缓存
        categoryService.clearCategoryTreeCache();

        // 记录审计日志
        recordAuditLog(adminUserId, categoryId, "category", "update",
                String.format("管理员修改分类[%s]", category.getName()));

        return convertToCategoryVO(category);
    }

    /**
     * 删除分类
     */
    @Transactional
    public void deleteCategory(Long categoryId, Long adminUserId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查是否有子分类
        CategoryExample childExample = new CategoryExample();
        childExample.createCriteria().andParentIdEqualTo(categoryId);
        long childCount = categoryMapper.countByExample(childExample);
        if (childCount > 0) {
            throw new RuntimeException("该分类下有子分类，请先删除子分类");
        }

        // 检查是否有商品使用该分类
        ProductCategoryExample pcExample = new ProductCategoryExample();
        pcExample.createCriteria().andCategoryIdEqualTo(categoryId);
        long productCount = productCategoryMapper.countByExample(pcExample);
        if (productCount > 0) {
            throw new RuntimeException("该分类下有商品，无法删除");
        }

        // 删除分类
        categoryMapper.deleteByPrimaryKey(categoryId);

        // 清除分类树缓存
        categoryService.clearCategoryTreeCache();

        // 记录审计日志
        recordAuditLog(adminUserId, categoryId, "category", "delete",
                String.format("管理员删除分类[%s]", category.getName()));
    }

    /**
     * 查看分类树（使用 common 模块的 CategoryService）
     */
    public List<CategoryVO> getCategoryTree() {
        // 从 common 模块获取分类树
        List<com.macro.mall.common.domain.CategoryVO> commonTree = categoryService.getCategoryTree();
        
        // 转换为 admin 模块的 CategoryVO
        return convertToAdminCategoryVOList(commonTree);
    }

    /**
     * 将 common 模块的 CategoryVO 转换为 admin 模块的 CategoryVO
     */
    private List<CategoryVO> convertToAdminCategoryVOList(List<com.macro.mall.common.domain.CategoryVO> commonTree) {
        return commonTree.stream()
                .map(this::convertToAdminCategoryVO)
                .collect(Collectors.toList());
    }

    /**
     * 将 common 模块的 CategoryVO 转换为 admin 模块的 CategoryVO（递归）
     */
    private CategoryVO convertToAdminCategoryVO(com.macro.mall.common.domain.CategoryVO commonVO) {
        CategoryVO adminVO = new CategoryVO();
        adminVO.setCategoryId(commonVO.getCategoryId());
        adminVO.setParentId(commonVO.getParentId());
        adminVO.setName(commonVO.getName());
        adminVO.setSortOrder(commonVO.getSortOrder());
        adminVO.setCreateTime(commonVO.getCreateTime());
        adminVO.setUpdateTime(commonVO.getUpdateTime());
        
        // 递归转换子分类
        if (commonVO.getChildren() != null && !commonVO.getChildren().isEmpty()) {
            adminVO.setChildren(commonVO.getChildren().stream()
                    .map(this::convertToAdminCategoryVO)
                    .collect(Collectors.toList()));
        }
        
        return adminVO;
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
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUserId, Long targetId, String targetType,
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUserId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }
}

