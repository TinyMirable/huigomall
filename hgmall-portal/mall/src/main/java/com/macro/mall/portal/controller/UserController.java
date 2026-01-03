package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.SendEmailCodeRequest;
import com.macro.mall.common.domain.UpdateEmailRequest;
import com.macro.mall.common.domain.UpdatePhoneRequest;
import com.macro.mall.common.domain.UpdateUsernameRequest;
import com.macro.mall.common.domain.UpdateUsernameResponse;
import com.macro.mall.common.domain.UserSummaryVO;
import com.macro.mall.portal.service.UserService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户概要信息
     * GET /api/users/me/summary
     * 
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 用户概要信息
     */
    @GetMapping("/users/me/summary")
    public CommonResult<UserSummaryVO> getUserSummary(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从Authorization header中提取token
            String token = extractToken(authorization);
            if (token == null) {
                return CommonResult.failed(401, "未提供认证token");
            }

            // 验证token并获取userId
            if (!JwtUtil.validateToken(token)) {
                return CommonResult.failed(401, "token无效或已过期");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return CommonResult.failed(401, "无法从token中获取用户ID");
            }

            // 获取用户概要信息
            UserSummaryVO summary = userService.getUserSummary(userId);
            if (summary == null) {
                return CommonResult.failed("用户不存在");
            }

            return CommonResult.success("获取成功", summary);
        } catch (Exception e) {
            return CommonResult.failed("获取用户概要信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户名
     * POST /api/users/me/username
     * 需要用户已登录（需要Authorization token）
     */
    @PostMapping("/users/me/username")
    public CommonResult<UpdateUsernameResponse> updateUsername(
            @Valid @RequestBody UpdateUsernameRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从Authorization header中提取token
            String token = extractToken(authorization);
            if (token == null) {
                return CommonResult.failed(401, "未提供认证token");
            }

            // 验证token并获取userId
            if (!JwtUtil.validateToken(token)) {
                return CommonResult.failed(401, "token无效或已过期");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return CommonResult.failed(401, "无法从token中获取用户ID");
            }

            // 修改用户名
            String updatedUsername = userService.updateUsername(userId, request.getUsername());

            // 返回更新后的用户名
            UpdateUsernameResponse response = new UpdateUsernameResponse(updatedUsername);
            return CommonResult.success("用户名修改成功", response);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("修改用户名失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮箱验证码（用于修改邮箱）
     * POST /api/users/me/email/send-code
     * 需要用户已登录（需要Authorization token）
     */
    @PostMapping("/users/me/email/send-code")
    public CommonResult<String> sendEmailCodeForUpdate(
            @Valid @RequestBody SendEmailCodeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从Authorization header中提取token
            String token = extractToken(authorization);
            if (token == null) {
                return CommonResult.failed(401, "未提供认证token");
            }

            // 验证token并获取userId
            if (!JwtUtil.validateToken(token)) {
                return CommonResult.failed(401, "token无效或已过期");
            }

            // 发送验证码到新邮箱
            userService.sendEmailCodeForUpdate(request.getEmail());
            return CommonResult.success("验证码已发送到您的邮箱，请查收", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("发送验证码失败: " + e.getMessage());
        }
    }

    /**
     * 修改邮箱
     * PUT /api/users/me/email
     * 需要用户已登录（需要Authorization token）
     * 普通用户和商家需要验证码，管理员不需要
     */
    @PutMapping("/users/me/email")
    public CommonResult<String> updateEmail(
            @Valid @RequestBody UpdateEmailRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从Authorization header中提取token
            String token = extractToken(authorization);
            if (token == null) {
                return CommonResult.failed(401, "未提供认证token");
            }

            // 验证token并获取userId
            if (!JwtUtil.validateToken(token)) {
                return CommonResult.failed(401, "token无效或已过期");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return CommonResult.failed(401, "无法从token中获取用户ID");
            }

            // 检查是否为管理员
            String roleCode = JwtUtil.getRoleCodeFromToken(token);
            boolean isAdmin = "ADMIN".equals(roleCode);

            // 修改邮箱
            String updatedEmail = userService.updateEmail(userId, request.getEmail(), request.getCode(), isAdmin);
            return CommonResult.success("邮箱修改成功", updatedEmail);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("修改邮箱失败: " + e.getMessage());
        }
    }

    /**
     * 修改手机号
     * PUT /api/users/me/phone
     * 需要用户已登录（需要Authorization token）
     * 普通用户和商家需要邮箱验证码（如果用户没有邮箱，提示先设置邮箱），管理员不需要
     */
    @PutMapping("/users/me/phone")
    public CommonResult<String> updatePhone(
            @Valid @RequestBody UpdatePhoneRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从Authorization header中提取token
            String token = extractToken(authorization);
            if (token == null) {
                return CommonResult.failed(401, "未提供认证token");
            }

            // 验证token并获取userId
            if (!JwtUtil.validateToken(token)) {
                return CommonResult.failed(401, "token无效或已过期");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return CommonResult.failed(401, "无法从token中获取用户ID");
            }

            // 检查是否为管理员
            String roleCode = JwtUtil.getRoleCodeFromToken(token);
            boolean isAdmin = "ADMIN".equals(roleCode);

            // 修改手机号
            String updatedPhone = userService.updatePhoneNumber(userId, request.getPhoneNumber(), request.getCode(), isAdmin);
            return CommonResult.success("手机号修改成功", updatedPhone);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("修改手机号失败: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token
     * 支持格式：Bearer {token} 或直接 {token}
     * 
     * @param authorization Authorization header值
     * @return token字符串，如果格式不正确返回null
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String trimmed = authorization.trim();
        
        // 如果以"Bearer "开头，去掉前缀
        if (trimmed.startsWith("Bearer ") || trimmed.startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        
        // 否则直接返回（支持直接传token的情况）
        return trimmed;
    }
}

