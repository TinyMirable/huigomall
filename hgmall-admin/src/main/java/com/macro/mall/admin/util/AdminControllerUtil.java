package com.macro.mall.admin.util;

import com.macro.mall.common.util.JwtUtil;

/**
 * 管理员控制器工具类
 * 提供通用的权限验证和token提取方法
 */
public class AdminControllerUtil {

    /**
     * 从Authorization header中提取token
     */
    public static String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String trimmed = authorization.trim();
        if (trimmed.startsWith("Bearer ") || trimmed.startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    /**
     * 从token中获取用户ID
     */
    public static Long getUserIdFromToken(String authorization) {
        String token = extractToken(authorization);
        if (token == null || !JwtUtil.validateToken(token)) {
            return null;
        }
        return JwtUtil.getUserIdFromToken(token);
    }

    /**
     * 验证管理员权限
     */
    public static void validateAdminRole(String authorization) {
        String token = extractToken(authorization);
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("token无效");
        }

        String roleCode = JwtUtil.getRoleCodeFromToken(token);
        if (roleCode == null || !"ADMIN".equals(roleCode)) {
            throw new RuntimeException("无权限执行此操作");
        }
    }
}

