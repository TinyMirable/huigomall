package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.CategoryVO;
import com.macro.mall.admin.domain.CreateCategoryRequest;
import com.macro.mall.admin.domain.UpdateCategoryRequest;
import com.macro.mall.admin.service.AdminCategoryService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    /**
     * 创建分类
     * POST /api/admin/categories
     */
    @PostMapping
    public CommonResult<CategoryVO> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            // sortOrder参数已废弃，优先级会自动根据分类级别生成
            CategoryVO category = adminCategoryService.createCategory(
                    request.getParentId(), request.getName(), null, adminUserId);
            return CommonResult.success("创建成功", category);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建分类失败: " + e.getMessage());
        }
    }

    /**
     * 修改分类
     * PUT /api/admin/categories/{categoryId}
     */
    @PutMapping("/{categoryId}")
    public CommonResult<CategoryVO> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            // sortOrder参数已废弃，优先级会自动根据分类级别生成
            CategoryVO category = adminCategoryService.updateCategory(
                    categoryId, request.getName(), request.getParentId(), adminUserId);
            return CommonResult.success("修改成功", category);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("修改分类失败: " + e.getMessage());
        }
    }

    /**
     * 删除分类
     * DELETE /api/admin/categories/{categoryId}
     */
    @DeleteMapping("/{categoryId}")
    public CommonResult<Void> deleteCategory(
            @PathVariable Long categoryId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            adminCategoryService.deleteCategory(categoryId, adminUserId);
            return CommonResult.success("删除成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("删除分类失败: " + e.getMessage());
        }
    }

    /**
     * 查看分类树
     * GET /api/admin/categories/tree
     */
    @GetMapping("/tree")
    public CommonResult<List<CategoryVO>> getCategoryTree(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            List<CategoryVO> tree = adminCategoryService.getCategoryTree();
            return CommonResult.success("获取成功", tree);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取分类树失败: " + e.getMessage());
        }
    }
}

