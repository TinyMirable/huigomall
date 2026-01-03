package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.CloseShopRequest;
import com.macro.mall.common.domain.CreateShopRequest;
import com.macro.mall.common.domain.ResumeShopRequest;
import com.macro.mall.common.domain.UpdateShopRequest;
import com.macro.mall.common.domain.ShopVO;
import com.macro.mall.portal.service.SensitiveOperationService;
import com.macro.mall.common.service.ShopService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家店铺管理控制器
 */
@RestController
@RequestMapping("/api/merchant/shops")
public class MerchantShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private SensitiveOperationService sensitiveOperationService;

    /**
     * 创建店铺
     * POST /api/merchant/shops
     * 需要商家身份验证
     * 
     * @param request 创建店铺请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 创建的店铺信息
     */
    @PostMapping
    public CommonResult<ShopVO> createShop(
            @Valid @RequestBody CreateShopRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 创建店铺
            ShopVO shop = shopService.createShop(merchantId, request.getName(), request.getDescription());
            return CommonResult.success("店铺创建成功", shop);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建店铺失败: " + e.getMessage());
        }
    }

    /**
     * 查询商家名下的所有店铺
     * GET /api/merchant/shops
     * 需要商家身份验证
     * 
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 店铺列表
     */
    @GetMapping
    public CommonResult<List<ShopVO>> getShops(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 查询店铺列表
            List<ShopVO> shops = shopService.getShopsByMerchant(merchantId);
            return CommonResult.success("获取成功", shops);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取店铺列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新店铺信息（名称和描述）
     * PUT /api/merchant/shops/{shopId}
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param shopId 店铺ID
     * @param request 更新店铺请求（包含名称、描述和验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的店铺信息
     */
    @PutMapping("/{shopId}")
    public CommonResult<ShopVO> updateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody UpdateShopRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 更新店铺
            ShopVO shop = shopService.updateShop(merchantId, shopId, request.getName(), request.getDescription(), userId);
            return CommonResult.success("店铺更新成功", shop);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新店铺失败: " + e.getMessage());
        }
    }

    /**
     * 关闭店铺（逻辑关闭）
     * POST /api/merchant/shops/{shopId}/close
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param shopId 店铺ID
     * @param request 关闭店铺请求（包含验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 操作结果
     */
    @PostMapping("/{shopId}/close")
    public CommonResult<Void> closeShop(
            @PathVariable Long shopId,
            @Valid @RequestBody CloseShopRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 关闭店铺
            shopService.closeShop(merchantId, shopId, userId);
            return CommonResult.success("店铺关闭成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("关闭店铺失败: " + e.getMessage());
        }
    }

    /**
     * 重新开启店铺（从 CLOSED 恢复到 ACTIVE）
     * POST /api/merchant/shops/{shopId}/resume
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param shopId 店铺ID
     * @param request 重新开启店铺请求（包含验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的店铺信息
     */
    @PostMapping("/{shopId}/resume")
    public CommonResult<ShopVO> resumeShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ResumeShopRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 重新开启店铺
            ShopVO shop = shopService.resumeShop(merchantId, shopId, userId);
            return CommonResult.success("店铺重新开启成功", shop);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("重新开启店铺失败: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String token = extractToken(authorization);
        if (token == null || !JwtUtil.validateToken(token)) {
            return null;
        }

        return JwtUtil.getUserIdFromToken(token);
    }

    /**
     * 从Authorization header中提取token
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String trimmed = authorization.trim();
        if (trimmed.startsWith("Bearer ") || trimmed.startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}

