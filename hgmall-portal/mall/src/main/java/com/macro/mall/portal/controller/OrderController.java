package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.BatchOrderVO;
import com.macro.mall.common.domain.CreateOrderRequest;
import com.macro.mall.common.domain.PayOrderRequest;
import com.macro.mall.portal.service.OrderService;
import com.macro.mall.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（从购物车）
     * POST /api/orders
     * 
     * @param request 创建订单请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 批量订单信息
     */
    @PostMapping
    public CommonResult<BatchOrderVO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            BatchOrderVO batchOrder = orderService.createOrder(userId, request);
            return CommonResult.success("订单创建成功", batchOrder);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的所有批量订单列表
     * GET /api/orders
     * 
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 批量订单列表
     */
    @GetMapping
    public CommonResult<List<BatchOrderVO>> getBatchOrderList(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            List<BatchOrderVO> batchOrders = orderService.getBatchOrderList(userId);
            return CommonResult.success("获取成功", batchOrders);
        } catch (Exception e) {
            return CommonResult.failed("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取批量订单详情
     * GET /api/orders/{batchOrderId}
     * 
     * @param batchOrderId 批量订单ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 批量订单详情
     */
    @GetMapping("/{batchOrderId}")
    public CommonResult<BatchOrderVO> getBatchOrderDetail(
            @PathVariable Long batchOrderId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            BatchOrderVO batchOrder = orderService.getBatchOrderDetail(batchOrderId, userId);
            return CommonResult.success("获取成功", batchOrder);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 支付订单
     * POST /api/orders/{batchOrderId}/pay
     * 
     * @param batchOrderId 批量订单ID
     * @param request 支付订单请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 支付后的订单信息
     */
    @PostMapping("/{batchOrderId}/pay")
    public CommonResult<BatchOrderVO> payOrder(
            @PathVariable Long batchOrderId,
            @Valid @RequestBody PayOrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证batchOrderId是否匹配
            if (!batchOrderId.equals(request.getBatchOrderId())) {
                return CommonResult.failed("订单ID不匹配");
            }

            BatchOrderVO batchOrder = orderService.payOrder(batchOrderId, userId);
            return CommonResult.success("支付成功", batchOrder);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("支付失败: " + e.getMessage());
        }
    }

    /**
     * 取消订单
     * POST /api/orders/{batchOrderId}/cancel
     * 
     * @param batchOrderId 批量订单ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 取消结果
     */
    @PostMapping("/{batchOrderId}/cancel")
    public CommonResult<String> cancelOrder(
            @PathVariable Long batchOrderId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            orderService.cancelOrder(batchOrderId, userId);
            return CommonResult.success("订单取消成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("取消订单失败: " + e.getMessage());
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












