package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.ShopVO;
import com.macro.mall.common.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺控制器（用户侧）
 */
@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 查看店铺（仅 ACTIVE 状态）
     * GET /api/shops/{shopId}
     * 非 ACTIVE 店铺返回 404 或业务错误码
     * 
     * @param shopId 店铺ID
     * @return 店铺信息
     */
    @GetMapping("/{shopId}")
    public CommonResult<ShopVO> getShop(@PathVariable Long shopId) {
        try {
            ShopVO shop = shopService.getShop(shopId);
            return CommonResult.success("获取成功", shop);
        } catch (RuntimeException e) {
            // 店铺不存在或非 ACTIVE 状态
            return CommonResult.failed(404, e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取店铺信息失败: " + e.getMessage());
        }
    }
}







