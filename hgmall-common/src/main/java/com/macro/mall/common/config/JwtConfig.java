package com.macro.mall.common.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 * 从 application.properties 读取 JWT 相关配置
 */
@Configuration
public class JwtConfig {

    /**
     * JWT 密钥
     */
    @Value("${jwt.secret:TheMallSecretKeyForJWTTokenGeneration2024SecureKey}")
    private String secret;

    /**
     * JWT Token 有效期（毫秒）
     */
    @Value("${jwt.expiration:604800000}")
    private long expiration;

    /**
     * 静态变量，供 JwtUtil 使用
     */
    private static String staticSecret;
    private static long staticExpiration;

    @PostConstruct
    public void init() {
        staticSecret = this.secret;
        staticExpiration = this.expiration;
    }

    /**
     * 获取 JWT 密钥（供 JwtUtil 使用）
     */
    public static String getSecret() {
        return staticSecret != null ? staticSecret : "TheMallSecretKeyForJWTTokenGeneration2024SecureKey";
    }

    /**
     * 获取 JWT Token 有效期（供 JwtUtil 使用）
     */
    public static long getExpiration() {
        return staticExpiration > 0 ? staticExpiration : 604800000L;
    }
}

