package com.macro.mall.admin.controller;

import com.macro.mall.admin.domain.CancelOrderRequest;
import com.macro.mall.admin.service.AdminOrderService;
import com.macro.mall.admin.util.AdminControllerUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.MerchantOrderListVO;
import com.macro.mall.common.domain.MerchantOrderVO;
import com.macro.mall.common.domain.OrderStatisticsVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员订单管理控制器
 */
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    /**
     * 查看所有订单列表
     * GET /api/admin/orders
     */
    @GetMapping
    public CommonResult<MerchantOrderListVO> getOrderList(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            MerchantOrderListVO result = adminOrderService.getOrderList(
                    shopId, merchantId, status, startTime, endTime, keyword, page, size);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 查看订单详情
     * GET /api/admin/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public CommonResult<MerchantOrderVO> getOrderDetail(
            @PathVariable Long orderId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            MerchantOrderVO order = adminOrderService.getOrderDetail(orderId);
            return CommonResult.success("获取成功", order);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 取消订单
     * POST /api/admin/orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public CommonResult<Void> cancelOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            Long adminUserId = AdminControllerUtil.getUserIdFromToken(authorization);
            if (adminUserId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            AdminControllerUtil.validateAdminRole(authorization);

            adminOrderService.cancelOrder(orderId, request.getReason(), adminUserId);
            return CommonResult.success("取消订单成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 查看订单统计
     * GET /api/admin/orders/statistics
     */
    @GetMapping("/statistics")
    public CommonResult<OrderStatisticsVO> getOrderStatistics(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            AdminControllerUtil.validateAdminRole(authorization);

            OrderStatisticsVO statistics = adminOrderService.getOrderStatistics(
                    shopId, merchantId, startTime, endTime);
            return CommonResult.success("获取成功", statistics);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取统计信息失败: " + e.getMessage());
        }
    }
}



