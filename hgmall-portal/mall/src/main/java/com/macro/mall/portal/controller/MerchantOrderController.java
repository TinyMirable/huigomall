package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.MerchantOrderListVO;
import com.macro.mall.common.domain.MerchantOrderVO;
import com.macro.mall.common.domain.OrderStatisticsVO;
import com.macro.mall.common.domain.ShipOrderRequest;
import com.macro.mall.portal.service.MerchantOrderService;
import com.macro.mall.common.service.ShopService;
import com.macro.mall.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 商家订单管理控制器
 */
@RestController
@RequestMapping("/api/merchant/orders")
public class MerchantOrderController {

    @Autowired
    private MerchantOrderService merchantOrderService;

    @Autowired
    private ShopService shopService;

    /**
     * 获取商家订单列表（分页、筛选）
     * GET /api/merchant/orders
     * 
     * @param shopId 店铺ID（可选，不传则查询所有店铺）
     * @param status 订单状态（可选，0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款）
     * @param startTime 开始时间（可选，ISO格式）
     * @param endTime 结束时间（可选，ISO格式）
     * @param page 页码（可选，默认1）
     * @param size 每页数量（可选，默认20）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 订单列表
     */
    @GetMapping
    public CommonResult<MerchantOrderListVO> getOrders(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 查询订单列表
            MerchantOrderListVO result = merchantOrderService.getMerchantOrders(
                    merchantId, shopId, status, startTime, endTime, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * GET /api/merchant/orders/{orderId}
     * 
     * @param orderId 订单ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public CommonResult<MerchantOrderVO> getOrderDetail(
            @PathVariable Long orderId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 查询订单详情
            MerchantOrderVO order = merchantOrderService.getOrderDetail(orderId, merchantId);
            return CommonResult.success("获取成功", order);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 订单发货
     * POST /api/merchant/orders/{orderId}/ship
     * 
     * @param orderId 订单ID
     * @param request 发货请求（包含备注）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的订单信息
     */
    @PostMapping("/{orderId}/ship")
    public CommonResult<MerchantOrderVO> shipOrder(
            @PathVariable Long orderId,
            @RequestBody(required = false) ShipOrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 发货
            String remark = request != null ? request.getRemark() : null;
            MerchantOrderVO order = merchantOrderService.shipOrder(orderId, merchantId, remark);
            return CommonResult.success("发货成功", order);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("发货失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单统计报表
     * GET /api/merchant/orders/statistics
     * 
     * @param shopId 店铺ID（必选，0表示查询商家所有店铺的总报表）
     * @param startTime 开始时间（可选，ISO格式）
     * @param endTime 结束时间（可选，ISO格式）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public CommonResult<OrderStatisticsVO> getStatistics(
            @RequestParam Long shopId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 查询统计信息（shopId=0表示查询所有店铺）
            OrderStatisticsVO statistics = merchantOrderService.getOrderStatistics(
                    shopId, merchantId, startTime, endTime);
            return CommonResult.success("获取成功", statistics);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token
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
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }

        String trimmed = authorization.trim();
        if (trimmed.startsWith("Bearer ") || trimmed.startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}

