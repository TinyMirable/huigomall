package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.AddressVO;
import com.macro.mall.common.domain.CreateAddressRequest;
import com.macro.mall.common.domain.UpdateAddressRequest;
import com.macro.mall.portal.service.AddressService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取当前用户的所有地址列表
     * GET /api/addresses
     * 
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 地址列表
     */
    @GetMapping
    public CommonResult<List<AddressVO>> getAddressList(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            List<AddressVO> addresses = addressService.getAddressList(userId);
            return CommonResult.success("获取成功", addresses);
        } catch (Exception e) {
            return CommonResult.failed("获取地址列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个地址详情
     * GET /api/addresses/{addressId}
     * 
     * @param addressId 地址ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 地址信息
     */
    @GetMapping("/{addressId}")
    public CommonResult<AddressVO> getAddress(
            @PathVariable Long addressId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AddressVO address = addressService.getAddress(addressId, userId);
            if (address == null) {
                return CommonResult.failed("地址不存在或无权访问");
            }

            return CommonResult.success("获取成功", address);
        } catch (Exception e) {
            return CommonResult.failed("获取地址详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新地址
     * POST /api/addresses
     * 
     * @param request 创建地址请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 创建的地址信息
     */
    @PostMapping
    public CommonResult<AddressVO> createAddress(
            @Valid @RequestBody CreateAddressRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AddressVO address = addressService.createAddress(
                userId,
                request.getDetail(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getIsDefault()
            );

            return CommonResult.success("地址创建成功", address);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建地址失败: " + e.getMessage());
        }
    }

    /**
     * 更新地址
     * PUT /api/addresses/{addressId}
     * 
     * @param addressId 地址ID
     * @param request 更新地址请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的地址信息
     */
    @PutMapping("/{addressId}")
    public CommonResult<AddressVO> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AddressVO address = addressService.updateAddress(
                addressId,
                userId,
                request.getDetail(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getIsDefault()
            );

            return CommonResult.success("地址更新成功", address);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新地址失败: " + e.getMessage());
        }
    }

    /**
     * 删除地址
     * DELETE /api/addresses/{addressId}
     * 
     * @param addressId 地址ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 删除结果
     */
    @DeleteMapping("/{addressId}")
    public CommonResult<String> deleteAddress(
            @PathVariable Long addressId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            addressService.deleteAddress(addressId, userId);
            return CommonResult.success("地址删除成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("删除地址失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认地址
     * PUT /api/addresses/{addressId}/set-default
     * 
     * @param addressId 地址ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的地址信息
     */
    @PutMapping("/{addressId}/set-default")
    public CommonResult<AddressVO> setDefaultAddress(
            @PathVariable Long addressId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AddressVO address = addressService.setDefaultAddress(addressId, userId);
            return CommonResult.success("设置默认地址成功", address);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("设置默认地址失败: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token并获取用户ID
     * 
     * @param authorization Authorization header值
     * @return 用户ID，如果token无效返回null
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String token = extractToken(authorization);
        if (token == null || !JwtUtil.validateToken(token)) {
            return null;
        }

        return JwtUtil.getUserIdFromToken(token);
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











