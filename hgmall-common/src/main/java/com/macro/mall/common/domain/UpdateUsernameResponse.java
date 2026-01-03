package com.macro.mall.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户名响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsernameResponse {
    /**
     * 更新后的用户名
     */
    private String username;
}











