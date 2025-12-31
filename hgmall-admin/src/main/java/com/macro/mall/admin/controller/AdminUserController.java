package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.UpdateUserStatusRequest;
import com.macro.mall.admin.domain.UserListVO;
import com.macro.mall.admin.domain.UserVO;
import com.macro.mall.admin.service.AdminUserService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.MerchantOrderListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 查看用户列表
     * GET /api/admin/users
     */
    @GetMapping
    public CommonResult<UserListVO> getUserList(
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            UserListVO result = adminUserService.getUserList(roleId, status, keyword, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 查看用户详情
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public CommonResult<UserVO> getUserDetail(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            UserVO user = adminUserService.getUserDetail(userId);
            return CommonResult.success("获取成功", user);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 管理用户状态
     * POST /api/admin/users/{userId}/status
     */
    @PostMapping("/{userId}/status")
    public CommonResult<Void> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            adminUserService.updateUserStatus(userId, request.getStatus(), request.getReason(), adminUserId);
            return CommonResult.success("状态更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 查看用户订单历史
     * GET /api/admin/users/{userId}/orders
     */
    @GetMapping("/{userId}/orders")
    public CommonResult<MerchantOrderListVO> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            MerchantOrderListVO result = adminUserService.getUserOrders(userId, status, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取用户订单失败: " + e.getMessage());
        }
    }
}






