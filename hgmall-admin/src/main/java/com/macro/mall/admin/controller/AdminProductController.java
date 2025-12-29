package com.macro.mall.admin.controller;

import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.service.ProductManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员商品管理控制器（admin模块）
 * 管理员可以修改商品状态（商品上下架），不需要验证码验证
 */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductManagementService productManagementService;

    /**
     * 管理员修改商品状态（上架/下架/强制下架）
     * PUT /api/admin/products/{productId}/status
     * 不需要验证码验证
     * 
     * @param productId 商品ID
     * @param status 新状态（1-上架，0-下架，2-管理员强制下架）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 操作结果
     */
    @PutMapping("/{productId}/status")
    public CommonResult<Void> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证管理员权限
            AdminControllerUtil.validateAdminRole(authorization);

            // 验证状态值（管理员可以设置为 ON_SHELF、OFF_SHELF 或 ADMIN_OFF_SHELF）
            if (status != ProductStatus.ON_SHELF && status != ProductStatus.OFF_SHELF && status != ProductStatus.ADMIN_OFF_SHELF) {
                return CommonResult.failed("无效的商品状态");
            }

            // 更新商品状态（管理员操作不需要验证码）
            productManagementService.updateProductStatusByAdmin(productId, status, userId);
            return CommonResult.success("商品状态更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新商品状态失败: " + e.getMessage());
        }
    }
}

