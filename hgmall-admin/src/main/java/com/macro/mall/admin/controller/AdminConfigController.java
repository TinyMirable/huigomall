package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.SystemConfigVO;
import com.macro.mall.admin.domain.UpdateConfigRequest;
import com.macro.mall.admin.service.AdminConfigService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员系统配置控制器
 */
@RestController
@RequestMapping("/api/admin/config")
public class AdminConfigController {

    @Autowired
    private AdminConfigService adminConfigService;

    /**
     * 获取系统配置
     * GET /api/admin/config
     */
    @GetMapping
    public CommonResult<SystemConfigVO> getSystemConfig(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            SystemConfigVO config = adminConfigService.getSystemConfig();
            return CommonResult.success("获取成功", config);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新商家审核开关
     * PUT /api/admin/config/merchant-audit
     */
    @PutMapping("/merchant-audit")
    public CommonResult<Void> updateMerchantAudit(
            @Valid @RequestBody UpdateConfigRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            adminConfigService.updateMerchantAuditEnabled(request.getEnabled());
            return CommonResult.success("更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新店铺审核开关
     * PUT /api/admin/config/shop-audit
     */
    @PutMapping("/shop-audit")
    public CommonResult<Void> updateShopAudit(
            @Valid @RequestBody UpdateConfigRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            adminConfigService.updateShopAuditEnabled(request.getEnabled());
            return CommonResult.success("更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新配置失败: " + e.getMessage());
        }
    }
}











