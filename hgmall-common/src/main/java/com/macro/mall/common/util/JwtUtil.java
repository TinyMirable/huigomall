package com.macro.mall.common.util;

import com.macro.mall.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 * 配置从 application.properties 中的 jwt.secret 和 jwt.expiration 读取
 */
public class JwtUtil {

    /**
     * 获取 JWT 密钥（从配置文件读取）
     */
    private static String getSecretKey() {
        return JwtConfig.getSecret();
    }

    /**
     * 获取 Token 有效期（从配置文件读取）
     */
    private static long getExpirationTime() {
        return JwtConfig.getExpiration();
    }

    /**
     * 生成 SecretKey
     */
    private static SecretKey getSigningKey() {
        String secretKey = getSecretKey();
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param roleCode 角色编码
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username, String roleCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleCode", roleCode);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getExpirationTime()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中获取 Claims
     * 
     * @param token JWT Token
     * @return Claims 对象
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户ID
     * 
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     * 
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取角色编码
     * 
     * @param token JWT Token
     * @return 角色编码
     */
    public static String getRoleCodeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("roleCode");
    }

    /**
     * 验证 Token 是否有效
     * 
     * @param token JWT Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查 Token 是否过期
     * 
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

