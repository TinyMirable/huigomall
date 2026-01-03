package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.CreateProductRequest;
import com.macro.mall.common.domain.ShopProductListVO;
import com.macro.mall.common.domain.ShopProductVO;
import com.macro.mall.common.domain.ShopProductDetailVO;
import com.macro.mall.common.domain.UpdateProductRequest;
import com.macro.mall.common.domain.UpdateProductStatusRequest;
import com.macro.mall.common.domain.UpdateSkuImageRequest;
import com.macro.mall.common.domain.UpdateSkuStockRequest;
import com.macro.mall.common.domain.UpdateSkuPriceRequest;
import com.macro.mall.common.domain.AddSkuRequest;
import com.macro.mall.common.domain.SkuVO;
import java.util.List;
import com.macro.mall.common.service.ProductManagementService;
import com.macro.mall.portal.service.SensitiveOperationService;
import com.macro.mall.common.service.ShopService;
import com.macro.mall.common.util.JwtUtil;
import com.macro.mall.mapper.RoleMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.Role;
import com.macro.mall.model.Usr;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺商品控制器（portal模块）
 * 提供查看店铺商品、商家商品管理功能
 */
@RestController
public class ShopProductController {

    @Autowired
    private ProductManagementService productManagementService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SensitiveOperationService sensitiveOperationService;

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 查看店铺商品（分页，支持筛选）
     * GET /api/shops/{shopId}/products
     * 
     * 权限规则：
     * - 所有人都可以查看正常营业店铺的商品
     * - 商家和管理员可以查看已关闭的店铺商品
     * 
     * @param shopId 店铺ID
     * @param status 商品状态（可选，1-上架，0-下架，null-全部）
     * @param categories 分类ID列表（可选，支持多个分类ID，如：?categories=1&categories=2）
     * @param page 页码（可选，默认1）
     * @param size 每页数量（可选，默认20）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 商品列表
     */
    @GetMapping("/api/shops/{shopId}/products")
    public CommonResult<ShopProductListVO> getShopProducts(
            @PathVariable Long shopId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) java.util.List<Long> categories,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 获取当前用户信息（可选）
            Long userId = null;
            String roleCode = null;
            if (authorization != null && !authorization.trim().isEmpty()) {
                String token = extractToken(authorization);
                if (token != null && JwtUtil.validateToken(token)) {
                    userId = JwtUtil.getUserIdFromToken(token);
                    Usr user = usrMapper.selectByPrimaryKey(userId);
                    if (user != null) {
                        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
                        if (role != null) {
                            roleCode = role.getRoleCode();
                        }
                    }
                }
            }

            ShopProductListVO result = productManagementService.getShopProducts(
                    shopId, status, categories, page, size, userId, roleCode);
            return CommonResult.success("获取成功", result);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取店铺商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 商家添加商品
     * POST /api/merchant/shops/{shopId}/products
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param shopId 店铺ID
     * @param request 创建商品请求
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 创建的商品信息
     */
    @PostMapping("/api/merchant/shops/{shopId}/products")
    public CommonResult<ShopProductVO> createProduct(
            @PathVariable Long shopId,
            @Valid @RequestBody CreateProductRequest request,
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

            // 验证店铺所有权
            shopService.validateShopOwnership(shopId, merchantId);

            // 验证邮箱验证码（创建商品需要验证码）
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 创建商品
            ShopProductVO product = productManagementService.createProduct(
                    shopId, request.getName(), request.getDescription(), request.getImageUrl(),
                    request.getCategoryId(), request.getSkus(), merchantId, userId);
            return CommonResult.success("商品创建成功", product);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建商品失败: " + e.getMessage());
        }
    }

    /**
     * 商家上架商品
     * POST /api/merchant/products/{productId}/on-shelf
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param request 更新商品状态请求（包含验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 操作结果
     */
    @PostMapping("/api/merchant/products/{productId}/on-shelf")
    public CommonResult<Void> onShelfProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证状态值
            if (request.getStatus() == null || request.getStatus() != 1) {
                return CommonResult.failed("商品状态无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证商品所有权
            productManagementService.validateProductOwnership(productId, merchantId);

            // 上架商品
            productManagementService.onShelfProduct(productId, merchantId, userId);
            return CommonResult.success("商品上架成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("商品上架失败: " + e.getMessage());
        }
    }

    /**
     * 商家下架商品
     * POST /api/merchant/products/{productId}/off-shelf
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param request 更新商品状态请求（包含验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 操作结果
     */
    @PostMapping("/api/merchant/products/{productId}/off-shelf")
    public CommonResult<Void> offShelfProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证状态值
            if (request.getStatus() == null || request.getStatus() != 0) {
                return CommonResult.failed("商品状态无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 验证商品所有权
            productManagementService.validateProductOwnership(productId, merchantId);

            // 下架商品
            productManagementService.offShelfProduct(productId, merchantId, userId);
            return CommonResult.success("商品下架成功", null);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("商品下架失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品详情（用于编辑）
     * GET /api/merchant/products/{productId}
     * 需要商家身份验证
     * 
     * @param productId 商品ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 商品详情
     */
    @GetMapping("/api/merchant/products/{productId}")
    public CommonResult<ShopProductDetailVO> getProductDetailForEdit(
            @PathVariable Long productId,
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

            // 获取商品详情
            ShopProductDetailVO productDetail = productManagementService.getProductDetailForEdit(productId, merchantId);
            return CommonResult.success("获取成功", productDetail);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取商品详情失败: " + e.getMessage());
        }
    }

    /**
     * 商家更新商品信息
     * PUT /api/merchant/products/{productId}
     * 需要商家身份验证（不再需要邮箱验证码）
     * 
     * @param productId 商品ID
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的商品信息
     */
    @PutMapping("/api/merchant/products/{productId}")
    public CommonResult<ShopProductVO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证邮箱验证码
            // sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 更新商品基本信息（不再包含SKU更新）
            ShopProductVO product = productManagementService.updateProduct(
                    productId, request.getName(), request.getDescription(), request.getImageUrl(),
                    request.getCategoryId(), merchantId, userId);
            return CommonResult.success("商品更新成功", product);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新商品失败: " + e.getMessage());
        }
    }

    /**
     * 更新SKU图片
     * PUT /api/merchant/products/{productId}/skus/{skuId}/image
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param request 更新SKU图片请求（包含图片URL和验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的SKU信息
     */
    @PutMapping("/api/merchant/products/{productId}/skus/{skuId}/image")
    public CommonResult<SkuVO> updateSkuImage(
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @Valid @RequestBody UpdateSkuImageRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 更新SKU图片
            SkuVO sku = productManagementService.updateSkuImage(
                    productId, skuId, request.getImageUrl(), merchantId, userId);
            return CommonResult.success("SKU图片更新成功", sku);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新SKU图片失败: " + e.getMessage());
        }
    }

    /**
     * 更新SKU库存（原子操作）
     * POST /api/merchant/products/{productId}/skus/{skuId}/stock
     * 支持增加/减少库存，或直接设置为指定值（通常用于标记为无货即设为0）
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param request 更新SKU库存请求（包含delta或absoluteStock和验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的SKU信息
     */
    @PostMapping("/api/merchant/products/{productId}/skus/{skuId}/stock")
    public CommonResult<SkuVO> updateSkuStock(
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @Valid @RequestBody UpdateSkuStockRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 更新SKU库存
            SkuVO sku = productManagementService.updateSkuStock(
                    productId, skuId, request.getDelta(), request.getAbsoluteStock(), merchantId, userId);
            return CommonResult.success("SKU库存更新成功", sku);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新SKU库存失败: " + e.getMessage());
        }
    }

    /**
     * 添加新SKU
     * POST /api/merchant/products/{productId}/skus
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param request 添加SKU请求（包含SKU列表和验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 添加的SKU列表
     */
    @PostMapping("/api/merchant/products/{productId}/skus")
    public CommonResult<List<SkuVO>> addSku(
            @PathVariable Long productId,
            @Valid @RequestBody AddSkuRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 添加新SKU
            List<SkuVO> skus = productManagementService.addSku(
                    productId, request.getSkus(), merchantId, userId);
            return CommonResult.success("SKU添加成功", skus);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("添加SKU失败: " + e.getMessage());
        }
    }

    /**
     * 更新SKU价格
     * PUT /api/merchant/products/{productId}/skus/{skuId}/price
     * 需要商家身份验证和邮箱验证码验证
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param request 更新SKU价格请求（包含价格和验证码）
     * @param authorization Authorization header，格式为 "Bearer {token}"
     * @return 更新后的SKU信息
     */
    @PutMapping("/api/merchant/products/{productId}/skus/{skuId}/price")
    public CommonResult<SkuVO> updateSkuPrice(
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @Valid @RequestBody UpdateSkuPriceRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            // 从token中获取用户ID
            Long userId = getUserIdFromToken(authorization);
            if (userId == null) {
                return CommonResult.failed(401, "未提供认证token或token无效");
            }

            // 验证邮箱验证码
            sensitiveOperationService.verifyEmailCode(userId, request.getCode());

            // 获取商家ID
            Long merchantId = shopService.getMerchantIdByUserId(userId);

            // 更新SKU价格
            SkuVO sku = productManagementService.updateSkuPrice(
                    productId, skuId, request.getPrice(), merchantId, userId);
            return CommonResult.success("SKU价格更新成功", sku);
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("更新SKU价格失败: " + e.getMessage());
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

