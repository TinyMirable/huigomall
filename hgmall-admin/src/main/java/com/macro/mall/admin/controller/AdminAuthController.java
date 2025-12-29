package com.macro.mall.admin.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.CodeLoginRequest;
import com.macro.mall.common.domain.LoginRequest;
import com.macro.mall.common.domain.LoginResult;
import com.macro.mall.common.domain.VerificationCodeRequest;
import com.macro.mall.common.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证控制器
 * 支持用户名/邮箱/手机号+密码登录，以及邮箱/手机号+验证码登录
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    /**
     * 发送验证码（通用接口，用于管理员操作）
     * 支持邮箱
     * 需要验证用户存在且为管理员（用于忘记密码、修改信息等场景）
     */
    @PostMapping("/send-code")
    public CommonResult<String> sendVerificationCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            String message = authService.sendAdminCode(request.getContact());
            return CommonResult.success(message, null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 发送管理员登录验证码
     * 发送前会验证用户是否存在且为管理员
     */
    @PostMapping("/send-login-code")
    public CommonResult<String> sendLoginCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            String message = authService.sendAdminLoginCode(request.getContact());
            return CommonResult.success(message, null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 管理员密码登录
     * 支持用户名/邮箱/手机号+密码登录
     */
    @PostMapping("/login")
    public CommonResult<LoginResult> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResult result = authService.adminLoginByPassword(request.getIdentifier(), request.getPassword());
            return CommonResult.success("登录成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 管理员验证码登录
     * 支持邮箱/手机号+验证码登录
     */
    @PostMapping("/login-by-code")
    public CommonResult<LoginResult> loginByCode(@Valid @RequestBody CodeLoginRequest request) {
        try {
            LoginResult result = authService.adminLoginByCode(request.getContact(), request.getCode());
            return CommonResult.success("登录成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }
}

