package com.macro.mall.admin.controller;

import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.common.domain.ShopListVO;
import com.macro.mall.common.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员店铺管理控制器（admin模块）
 * 管理员可以查看所有店铺、修改店铺状态等，不需要验证码验证
 */
@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取所有店铺列表（支持筛选和分页）
     * GET /api/admin/shops
     * 
     * @param status 店铺状态（可选，1-正常，2-关闭，3-管理员强制关闭）
     * @param keyword 关键词（可选，搜索店铺名称）
     * @param page 页码（可选，默认1）
     * @param size 每页数量（可选，默认20）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 店铺列表
     */
    @GetMapping
    public CommonResult<ShopListVO> getShopList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 验证管理员权限
            AdminControllerUtil.validateAdminRole(authorization);

            ShopListVO result = shopService.getAllShops(status, keyword, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取店铺列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员修改店铺状态
     * PUT /api/admin/shops/{shopId}/status
     * 不需要验证码验证
     * 
     * @param shopId 店铺ID
     * @param status 新状态（1-正常，2-关闭，3-管理员强制关闭）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 操作结果
     */
    @PutMapping("/{shopId}/status")
    public CommonResult<Void> updateShopStatus(
            @PathVariable Long shopId,
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

            // 验证状态值
            if (status != ShopStatus.ACTIVE && status != ShopStatus.CLOSED && status != ShopStatus.ADMIN_CLOSED) {
                return CommonResult.failed("无效的店铺状态");
            }

            // 更新店铺状态
            shopService.updateShopStatus(shopId, status, userId);
            return CommonResult.success("店铺状态更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新店铺状态失败: " + e.getMessage());
        }
    }

}

