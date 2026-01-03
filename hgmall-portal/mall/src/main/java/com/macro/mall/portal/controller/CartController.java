package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.AddCartItemRequest;
import com.macro.mall.common.domain.CartItemVO;
import com.macro.mall.common.domain.UpdateCartItemRequest;
import com.macro.mall.portal.service.CartService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取当前用户的所有购物车项
     * GET /api/cart
     * 
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 购物车项列表
     */
    @GetMapping
    public CommonResult<List<CartItemVO>> getCartItemList(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            List<CartItemVO> cartItems = cartService.getCartItemList(userId);
            return CommonResult.success("获取成功", cartItems);
        } catch (Exception e) {
            return CommonResult.failed("获取购物车列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个购物车项详情
     * GET /api/cart/{itemId}
     * 
     * @param itemId 购物车项ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 购物车项信息
     */
    @GetMapping("/{itemId}")
    public CommonResult<CartItemVO> getCartItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            CartItemVO cartItem = cartService.getCartItem(userId, itemId);
            if (cartItem == null) {
                return CommonResult.failed("购物车项不存在");
            }
            return CommonResult.success("获取成功", cartItem);
        } catch (Exception e) {
            return CommonResult.failed("获取购物车项失败: " + e.getMessage());
        }
    }

    /**
     * 添加购物车项
     * POST /api/cart
     * 
     * @param request 添加购物车项请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 购物车项信息
     */
    @PostMapping
    public CommonResult<CartItemVO> addCartItem(
            @Valid @RequestBody AddCartItemRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            CartItemVO cartItem = cartService.addCartItem(userId, request);
            return CommonResult.success("添加成功", cartItem);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("添加购物车项失败: " + e.getMessage());
        }
    }

    /**
     * 更新购物车项数量
     * PUT /api/cart/{itemId}
     * 
     * @param itemId 购物车项ID
     * @param request 更新购物车项请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的购物车项信息
     */
    @PutMapping("/{itemId}")
    public CommonResult<CartItemVO> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            CartItemVO cartItem = cartService.updateCartItem(userId, itemId, request);
            return CommonResult.success("更新成功", cartItem);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新购物车项失败: " + e.getMessage());
        }
    }

    /**
     * 删除购物车项
     * DELETE /api/cart/{itemId}
     * 
     * @param itemId 购物车项ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 删除结果
     */
    @DeleteMapping("/{itemId}")
    public CommonResult<String> deleteCartItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            cartService.deleteCartItem(userId, itemId);
            return CommonResult.success("删除成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("删除购物车项失败: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token并获取用户ID
     * 
     * @param authorization Authorization header
     * @return 用户ID，如果token无效则返回null
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring(7);
        if (!JwtUtil.validateToken(token)) {
            return null;
        }

        return JwtUtil.getUserIdFromToken(token);
    }
}












