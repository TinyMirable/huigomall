package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.*;
import com.macro.mall.common.service.AuthService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 发送验证码（通用接口，用于用户操作）
     * 支持手机号和邮箱
     * 需要验证用户存在（用于忘记密码、修改信息等场景）
     */
    @PostMapping("/send-code")
    public CommonResult<String> sendVerificationCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            String message = authService.sendUserCode(request.getContact());
            return CommonResult.success(message, null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 发送注册验证码
     * 支持手机号和邮箱
     * 需要验证用户不存在（仅用于注册）
     */
    @PostMapping("/send-register-code")
    public CommonResult<String> sendRegisterCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            String message = authService.sendRegisterCode(request.getContact());
            return CommonResult.success(message, null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 发送登录验证码
     * 发送前会验证用户是否存在
     */
    @PostMapping("/send-login-code")
    public CommonResult<String> sendLoginCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            String message = authService.sendUserLoginCode(request.getContact());
            return CommonResult.success(message, null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 用户密码登录
     * 支持用户名/邮箱/手机号+密码登录
     */
    @PostMapping("/login")
    public CommonResult<LoginResult> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResult result = authService.userLoginByPassword(request.getIdentifier(), request.getPassword());
            return CommonResult.success("登录成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 用户验证码登录
     */
    @PostMapping("/login-by-code")
    public CommonResult<LoginResult> loginByCode(@Valid @RequestBody CodeLoginRequest request) {
        try {
            LoginResult result = authService.userCodeLogin(request.getContact(), request.getCode());
            return CommonResult.success("登录成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 忘记密码（重置密码）
     */
    @PostMapping("/forgot-password")
    public CommonResult<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request.getEmail(), request.getCode(), request.getNewPassword());
            return CommonResult.success("密码重置成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public CommonResult<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getEmail(), request.getCode(), request.getNewPassword());
            return CommonResult.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public CommonResult<LoginResult> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResult result = authService.register(
                request.getUsername(), 
                request.getPassword(), 
                request.getEmail(), 
                request.getCode()
            );
            return CommonResult.success("注册成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 用户注册为商家
     * POST /api/auth/register-merchant
     * 需要用户已登录（需要Authorization token）
     */
    @PostMapping("/register-merchant")
    public CommonResult<String> registerMerchant(
            @Valid @RequestBody MerchantRegisterRequest request,
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

            // 注册为商家
            authService.registerAsMerchant(
                userId,
                request.getMerchantName(),
                request.getOwner()
            );

            return CommonResult.success("商家注册成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("商家注册失败: " + e.getMessage());
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
        
        // 移除Bearer前缀（如果存在）
        String token = authorization.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        
        return token.isEmpty() ? null : token;
    }
}

