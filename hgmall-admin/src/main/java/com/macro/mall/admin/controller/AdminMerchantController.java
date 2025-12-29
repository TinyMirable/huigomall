package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.AuditMerchantRequest;
import com.macro.mall.admin.domain.MerchantListVO;
import com.macro.mall.admin.domain.MerchantVO;
import com.macro.mall.admin.domain.UpdateMerchantStatusRequest;
import com.macro.mall.admin.service.AdminMerchantService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员商家管理控制器
 */
@RestController
@RequestMapping("/api/admin/merchants")
public class AdminMerchantController {

    @Autowired
    private AdminMerchantService adminMerchantService;


    /**
     * 查看商家详情
     * GET /api/admin/merchants/{merchantId}
     */
    @GetMapping("/{merchantId}")
    public CommonResult<MerchantVO> getMerchantDetail(
            @PathVariable Long merchantId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            MerchantVO merchant = adminMerchantService.getMerchantDetail(merchantId);
            return CommonResult.success("获取成功", merchant);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取商家详情失败: " + e.getMessage());
        }
    }

    /**
     * 审核商家申请
     * POST /api/admin/merchants/{merchantId}/audit
     */
    @PostMapping("/{merchantId}/audit")
    public CommonResult<Void> auditMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody AuditMerchantRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            adminMerchantService.auditMerchant(merchantId, request.getAction(), request.getReason(), adminUserId);
            return CommonResult.success("审核成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("审核失败: " + e.getMessage());
        }
    }

    /**
     * 管理商家状态
     * POST /api/admin/merchants/{merchantId}/status
     */
    @PostMapping("/{merchantId}/status")
    public CommonResult<Void> updateMerchantStatus(
            @PathVariable Long merchantId,
            @Valid @RequestBody UpdateMerchantStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            adminMerchantService.updateMerchantStatus(merchantId, request.getStatus(), request.getReason(), adminUserId);
            return CommonResult.success("状态更新成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新状态失败: " + e.getMessage());
        }
    }
}


