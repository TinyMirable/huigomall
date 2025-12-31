package com.macro.mall.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置
 * 允许验证码接口公开访问
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS
            .csrf(csrf -> csrf.disable()) // 禁用 CSRF（开发阶段）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll() // 允许健康检查端点公开访问
                .requestMatchers("/api/auth/**").permitAll() // 允许认证相关接口公开访问（包括注册、登录、验证码等）
                .requestMatchers("/api/home", "/api/products/**").permitAll() // 允许产品相关接口公开访问（首页、商品列表、商品详情）
                .requestMatchers("/api/categories/**").permitAll() // 允许分类相关接口公开访问
                .requestMatchers("/api/shops/**").permitAll() // 允许店铺相关接口公开访问（查看店铺、查看店铺商品，控制器内部会验证权限）
                .requestMatchers("/api/shops/**").permitAll() // 允许店铺相关接口访问（查看店铺、查看店铺商品，控制器内部会验证token和权限）
                .requestMatchers("/api/users/me/**").permitAll() // 允许用户相关接口访问（控制器内部会验证token）
                .requestMatchers("/api/addresses/**").permitAll() // 允许地址相关接口访问（控制器内部会验证token）
                .requestMatchers("/api/cart/**").permitAll() // 允许购物车相关接口访问（控制器内部会验证token）
                .requestMatchers("/api/orders/**").permitAll() // 允许订单相关接口访问（控制器内部会验证token）
                .requestMatchers("/api/merchant/shops/**").permitAll() // 允许商家店铺相关接口访问（控制器内部会验证token）
                .requestMatchers("/api/merchant/products/**").permitAll() // 允许商家商品管理接口访问（控制器内部会验证token）
                .requestMatchers("/api/merchant/orders/**").permitAll() // 允许商家订单相关接口访问（控制器内部会验证token）
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // 允许 Swagger UI 访问
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // 允许所有OPTIONS预检请求
                .anyRequest().authenticated() // 其他接口需要认证
            );
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 允许所有源（开发环境）
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


