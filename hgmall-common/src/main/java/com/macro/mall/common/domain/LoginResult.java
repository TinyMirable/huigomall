package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * Token（可选，如果使用JWT）
     */
    private String token;
}







