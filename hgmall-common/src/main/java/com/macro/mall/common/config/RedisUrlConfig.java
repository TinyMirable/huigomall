package com.macro.mall.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.net.URI;

/**
 * Redis URL配置类（可选）
 * 如果设置了REDIS_URL环境变量，会使用该URL来配置Redis连接
 * 需要在application.properties中设置: redis.url.enabled=true 来启用
 * 
 * 如果未启用，将使用标准的Spring Boot Redis配置（spring.data.redis.*）
 */
@Configuration
@ConditionalOnProperty(name = "redis.url.enabled", havingValue = "true", matchIfMissing = false)
public class RedisUrlConfig {

    /**
     * 从REDIS_URL环境变量解析Redis配置
     * 格式: redis://localhost:6379/0 或 redis://:password@localhost:6379/0
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        String redisUrl = System.getenv("REDIS_URL");
        if (redisUrl == null || redisUrl.isEmpty()) {
            throw new IllegalStateException("REDIS_URL environment variable is not set");
        }

        try {
            URI uri = new URI(redisUrl);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 6379 : uri.getPort();
            String path = uri.getPath();
            int database = 0;
            
            if (path != null && path.length() > 1) {
                database = Integer.parseInt(path.substring(1));
            }
            
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(host);
            config.setPort(port);
            config.setDatabase(database);
            
            // 如果URL包含密码，可以在这里设置
            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":");
                if (parts.length == 2) {
                    config.setPassword(parts[1]);
                }
            }
            
            return new LettuceConnectionFactory(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse REDIS_URL: " + redisUrl, e);
        }
    }
}

