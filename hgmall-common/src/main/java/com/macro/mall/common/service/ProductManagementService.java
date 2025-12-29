package com.macro.mall.common.service;

import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.common.domain.ShopProductListVO;
import com.macro.mall.common.domain.ShopProductVO;
import com.macro.mall.common.domain.ShopProductDetailVO;
import com.macro.mall.common.domain.SkuVO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 商品管理服务
 * 提供商品添加、上架、下架等功能
 */
@Service
public class ProductManagementService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String SHOP_PRODUCTS_KEY_PREFIX = "shop:products:";
    private static final String PRODUCT_DETAIL_KEY_PREFIX = "product:detail:";
    private static final String HOME_PAGE_KEY = "home:page";
    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";

    /**
     * 缓存TTL（秒）
     */
    private static final long SHOP_PRODUCTS_TTL = 60; // 1分钟

    /**
     * 查看店铺商品（分页，支持筛选）
     * 
     * @param shopId 店铺ID
     * @param status 商品状态（可选，null表示查询所有状态）
     * @param categoryIds 分类ID列表（可选，null或空表示查询所有分类）
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @param userId 当前用户ID（用于权限判断）
     * @param roleCode 当前用户角色（用于权限判断）
     * @return 商品列表
     * @throws RuntimeException 查询失败时抛出异常
     */
    public ShopProductListVO getShopProducts(Long shopId, Integer status, List<Long> categoryIds, Integer page, Integer size, 
                                              Long userId, String roleCode) {
        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 权限检查
        boolean canViewAllStatus = false;
        if ("ADMIN".equals(roleCode)) {
            // 管理员可以查看所有状态的店铺
            canViewAllStatus = true;
        } else if ("MERCHANT".equals(roleCode)) {
            // 商家可以查看自己店铺的所有状态商品
            Long merchantId = getMerchantIdByUserId(userId);
            if (shop.getMerchantId().equals(merchantId)) {
                canViewAllStatus = true;
            }
        }

        // 如果店铺不是ACTIVE状态，只有管理员和商家可以查看
        if (shop.getStatus() != null && shop.getStatus() != ShopStatus.ACTIVE) {
            if (!canViewAllStatus) {
                throw new RuntimeException("店铺不存在或已关闭");
            }
        }

        // 如果用户不是管理员或商家，只能查看上架商品
        if (!canViewAllStatus && status == null) {
            status = ProductStatus.ON_SHELF;
        }

        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 递归获取所有子分类ID（包含自身）
        List<Long> allCategoryIds = categoryService.getCategoryIdsWithChildren(categoryIds);

        // 构建缓存Key（包含店铺ID、状态、分类、页码、每页大小）
        String statusKey = status != null ? String.valueOf(status) : "all";
        String categoryKey = "all";
        if (allCategoryIds != null && !allCategoryIds.isEmpty()) {
            // 排序并去重，确保相同分类组合生成相同的缓存Key
            List<Long> sortedIds = new ArrayList<>(new java.util.TreeSet<>(allCategoryIds));
            categoryKey = sortedIds.stream()
                    .map(String::valueOf)
                    .collect(java.util.stream.Collectors.joining(","));
        }
        String cacheKey = SHOP_PRODUCTS_KEY_PREFIX + shopId + ":status=" + statusKey 
                + ":categories=" + categoryKey + ":page=" + page + ":size=" + size;

        // 尝试从缓存获取（只有普通用户查看上架商品时才使用缓存）
        if (!canViewAllStatus && status != null && status == ProductStatus.ON_SHELF) {
            ShopProductListVO cached = (ShopProductListVO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        // 查询商品列表（使用包含子分类的ID列表）
        List<Map<String, Object>> productMaps = productMapper.selectByShopIdWithPaging(shopId, status, allCategoryIds, offset, size);
        Long total = productMapper.countByShopId(shopId, status, allCategoryIds);

        // 转换为VO
        List<ShopProductVO> products = convertToShopProductVOList(productMaps);

        ShopProductListVO result = new ShopProductListVO();
        result.setProducts(products);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        // 存入缓存（只有普通用户查看上架商品时才缓存）
        if (!canViewAllStatus && status != null && status == ProductStatus.ON_SHELF) {
            long ttl = SHOP_PRODUCTS_TTL + new Random().nextInt(30);
            redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);
        }

        return result;
    }

    /**
     * 获取商品详情（用于编辑）
     * 
     * @param productId 商品ID
     * @param merchantId 商家ID（用于验证权限）
     * @return 商品详情
     * @throws RuntimeException 查询失败时抛出异常
     */
    public ShopProductDetailVO getProductDetailForEdit(Long productId, Long merchantId) {
        // 验证商品是否存在
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 验证店铺是否属于该商家
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null || !shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该商品");
        }

        // 查询分类ID
        ProductCategoryExample pcExample = new ProductCategoryExample();
        pcExample.createCriteria().andProductIdEqualTo(productId);
        List<ProductCategoryKey> productCategories = productCategoryMapper.selectByExample(pcExample);
        Long categoryId = null;
        if (productCategories != null && !productCategories.isEmpty()) {
            categoryId = productCategories.get(0).getCategoryId();
        }

        // 查询SKU列表
        SkuExample skuExample = new SkuExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        List<Sku> skus = skuMapper.selectByExample(skuExample);

        // 转换为SkuVO列表
        List<SkuVO> skuVOs = new ArrayList<>();
        for (Sku sku : skus) {
            SkuVO skuVO = new SkuVO();
            skuVO.setSkuId(sku.getSkuId());
            skuVO.setSpec(sku.getSpec());
            skuVO.setPrice(sku.getPrice());
            skuVO.setStock(sku.getStock());
            skuVO.setImageUrl(sku.getImageUrl());
            skuVOs.add(skuVO);
        }

        // 构建返回结果
        ShopProductDetailVO vo = new ShopProductDetailVO();
        vo.setProductId(product.getProductId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setImageUrl(product.getImageUrl());
        vo.setCategoryId(categoryId);
        vo.setSkus(skuVOs);

        return vo;
    }

    /**
     * 添加商品（初始为下架状态）
     * 
     * @param shopId 店铺ID
     * @param name 商品名称
     * @param description 商品描述
     * @param imageUrl 商品图片URL
     * @param categoryId 分类ID
     * @param skus SKU列表（至少需要一个）
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 创建的商品信息
     * @throws RuntimeException 创建失败时抛出异常
     */
    @Transactional
    public ShopProductVO createProduct(Long shopId, String name, String description, String imageUrl,
                                      Long categoryId, List<com.macro.mall.common.domain.CreateSkuRequest> skus,
                                      Long merchantId, Long operatorUserId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法创建商品");
        }

        // 验证店铺是否存在
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            throw new RuntimeException("店铺不存在");
        }

        // 验证店铺是否属于该商家
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该店铺");
        }

        // 验证店铺状态（只有ACTIVE状态的店铺才能添加商品）
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法添加商品");
        }

        // 验证分类是否存在
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 验证SKU列表
        if (skus == null || skus.isEmpty()) {
            throw new RuntimeException("至少需要添加一个SKU");
        }

        // 创建商品
        Product product = new Product();
        product.setShopId(shopId);
        product.setName(name);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setStatus(ProductStatus.OFF_SHELF); // 初始为下架状态
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insertSelective(product);

        // 关联商品和分类
        ProductCategoryKey productCategory = new ProductCategoryKey();
        productCategory.setProductId(product.getProductId());
        productCategory.setCategoryId(categoryId);
        productCategoryMapper.insert(productCategory);

        // 创建SKU列表
        for (com.macro.mall.common.domain.CreateSkuRequest skuRequest : skus) {
            Sku sku = new Sku();
            sku.setProductId(product.getProductId());
            sku.setSpec(skuRequest.getSpec());
            sku.setPrice(skuRequest.getPrice());
            sku.setStock(skuRequest.getStock());
            sku.setImageUrl(skuRequest.getImageUrl());
            sku.setCreateTime(LocalDateTime.now());
            sku.setUpdateTime(LocalDateTime.now());
            skuMapper.insertSelective(sku);
        }

        // 记录审计日志
        recordAuditLog(operatorUserId, product.getProductId(), "product", "create",
                String.format("商家[%d]在店铺[%d]添加商品[%s]到分类[%s]，包含%d个SKU", merchantId, shopId, name, category.getName(), skus.size()));

        // 清除店铺商品列表缓存
        clearShopProductsCache(shopId);

        return convertToShopProductVO(product);
    }

    /**
     * 更新商品信息
     * 
     * @param productId 商品ID
     * @param name 商品名称
     * @param description 商品描述
     * @param imageUrl 商品图片URL
     * @param categoryId 分类ID（可选，如果提供则更新分类关联）
     * @param skus SKU列表（可选，如果提供则完全替换现有SKU）
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的商品信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public ShopProductVO updateProduct(Long productId, String name, String description, String imageUrl,
                                       Long categoryId,
                                       Long merchantId, Long operatorUserId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法修改商品");
        }

        // 验证商品是否存在
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 验证店铺是否属于该商家
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null || !shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该商品");
        }

        // 验证店铺状态（只有ACTIVE状态的店铺才能修改商品）
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法修改商品");
        }

        // 更新商品基本信息
        if (name != null) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateByPrimaryKeySelective(product);

        // 更新分类关联（如果提供了分类ID）
        if (categoryId != null) {
            // 验证分类是否存在
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                throw new RuntimeException("分类不存在");
            }

            // 删除旧的分类关联
            ProductCategoryExample pcExample = new ProductCategoryExample();
            pcExample.createCriteria().andProductIdEqualTo(productId);
            productCategoryMapper.deleteByExample(pcExample);

            // 创建新的分类关联
            ProductCategoryKey productCategory = new ProductCategoryKey();
            productCategory.setProductId(productId);
            productCategory.setCategoryId(categoryId);
            productCategoryMapper.insert(productCategory);
        }

        // 记录审计日志
        StringBuilder logDesc = new StringBuilder();
        logDesc.append(String.format("商家[%d]更新商品[%s]", merchantId, product.getName()));
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            logDesc.append(String.format("，分类更新为[%s]", category != null ? category.getName() : categoryId));
        }
        recordAuditLog(operatorUserId, productId, "product", "update", logDesc.toString());

        // 清除店铺商品列表缓存
        clearShopProductsCache(product.getShopId());

        // 重新查询商品以获取最新信息
        Product updatedProduct = productMapper.selectByPrimaryKey(productId);
        return convertToShopProductVO(updatedProduct);
    }

    /**
     * 更新SKU图片
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param imageUrl 新的图片URL
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的SKU信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public SkuVO updateSkuImage(Long productId, Long skuId, String imageUrl,
                                Long merchantId, Long operatorUserId) {
        // 验证权限和商品
        validateProductOwnership(productId, merchantId);

        // 验证SKU是否存在且属于该商品
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }
        if (!sku.getProductId().equals(productId)) {
            throw new RuntimeException("SKU不属于该商品");
        }

        // 更新SKU图片
        sku.setImageUrl(imageUrl);
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateByPrimaryKeySelective(sku);

        // 记录审计日志
        recordAuditLog(operatorUserId, skuId, "sku", "update_image",
                String.format("商家[%d]更新SKU[%d]图片", merchantId, skuId));

        // 清除相关缓存
        Product product = productMapper.selectByPrimaryKey(productId);
        clearShopProductsCache(product.getShopId());
        clearProductCache(productId);

        // 转换为VO
        SkuVO vo = new SkuVO();
        vo.setSkuId(sku.getSkuId());
        vo.setSpec(sku.getSpec());
        vo.setPrice(sku.getPrice());
        vo.setStock(sku.getStock());
        vo.setImageUrl(sku.getImageUrl());
        return vo;
    }

    /**
     * 更新SKU库存（原子操作）
     * 支持增加/减少库存，或直接设置为指定值（通常用于标记为无货即设为0）
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param delta 库存变化量（正数表示增加，负数表示减少），如果为null则使用absoluteStock
     * @param absoluteStock 绝对库存值（如果提供，则直接设置为该值），如果为null则使用delta
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的SKU信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public SkuVO updateSkuStock(Long productId, Long skuId, Integer delta, Integer absoluteStock,
                                Long merchantId, Long operatorUserId) {
        // 验证权限和商品
        validateProductOwnership(productId, merchantId);

        // 验证SKU是否存在且属于该商品
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }
        if (!sku.getProductId().equals(productId)) {
            throw new RuntimeException("SKU不属于该商品");
        }

        // 确定新的库存值
        Integer newStock;
        if (absoluteStock != null) {
            // 直接设置为指定值
            if (absoluteStock < 0) {
                throw new RuntimeException("库存不能为负数");
            }
            newStock = absoluteStock;
        } else if (delta != null) {
            // 使用变化量
            int currentStock = sku.getStock() != null ? sku.getStock() : 0;
            newStock = currentStock + delta;
            if (newStock < 0) {
                throw new RuntimeException("库存不足，无法减少。当前库存：" + currentStock + "，尝试减少：" + Math.abs(delta));
            }
        } else {
            throw new RuntimeException("必须提供delta或absoluteStock之一");
        }

        // 原子更新库存（使用数据库UPDATE语句）
        sku.setStock(newStock);
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateByPrimaryKeySelective(sku);

        // 记录审计日志
        String operationDesc;
        if (absoluteStock != null) {
            operationDesc = String.format("商家[%d]将SKU[%d]库存设置为%d", merchantId, skuId, absoluteStock);
        } else {
            operationDesc = String.format("商家[%d]%sSKU[%d]库存%d，当前库存：%d", 
                    merchantId, delta > 0 ? "增加" : "减少", skuId, Math.abs(delta), newStock);
        }
        recordAuditLog(operatorUserId, skuId, "sku", "update_stock", operationDesc);

        // 清除相关缓存
        Product product = productMapper.selectByPrimaryKey(productId);
        clearShopProductsCache(product.getShopId());
        clearProductCache(productId);

        // 转换为VO
        SkuVO vo = new SkuVO();
        vo.setSkuId(sku.getSkuId());
        vo.setSpec(sku.getSpec());
        vo.setPrice(sku.getPrice());
        vo.setStock(sku.getStock());
        vo.setImageUrl(sku.getImageUrl());
        return vo;
    }

    /**
     * 添加新SKU
     * 
     * @param productId 商品ID
     * @param skus SKU列表
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 添加的SKU列表
     * @throws RuntimeException 添加失败时抛出异常
     */
    @Transactional
    public List<SkuVO> addSku(Long productId, List<com.macro.mall.common.domain.CreateSkuRequest> skus,
                              Long merchantId, Long operatorUserId) {
        // 验证权限和商品
        validateProductOwnership(productId, merchantId);

        // 验证SKU列表
        if (skus == null || skus.isEmpty()) {
            throw new RuntimeException("至少需要添加一个SKU");
        }

        // 创建新的SKU列表
        List<SkuVO> result = new ArrayList<>();
        for (com.macro.mall.common.domain.CreateSkuRequest skuRequest : skus) {
            Sku sku = new Sku();
            sku.setProductId(productId);
            sku.setSpec(skuRequest.getSpec());
            sku.setPrice(skuRequest.getPrice());
            sku.setStock(skuRequest.getStock());
            sku.setImageUrl(skuRequest.getImageUrl());
            sku.setCreateTime(LocalDateTime.now());
            sku.setUpdateTime(LocalDateTime.now());
            skuMapper.insertSelective(sku);

            // 转换为VO
            SkuVO vo = new SkuVO();
            vo.setSkuId(sku.getSkuId());
            vo.setSpec(sku.getSpec());
            vo.setPrice(sku.getPrice());
            vo.setStock(sku.getStock());
            vo.setImageUrl(sku.getImageUrl());
            result.add(vo);
        }

        // 记录审计日志
        recordAuditLog(operatorUserId, productId, "product", "add_sku",
                String.format("商家[%d]为商品[%d]添加%d个新SKU", merchantId, productId, skus.size()));

        // 清除相关缓存
        Product product = productMapper.selectByPrimaryKey(productId);
        clearShopProductsCache(product.getShopId());
        clearProductCache(productId);

        return result;
    }

    /**
     * 更新SKU价格
     * 
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param price 新价格
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @return 更新后的SKU信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    @Transactional
    public SkuVO updateSkuPrice(Long productId, Long skuId, BigDecimal price,
                                Long merchantId, Long operatorUserId) {
        // 验证权限和商品
        validateProductOwnership(productId, merchantId);

        // 验证SKU是否存在且属于该商品
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }
        if (!sku.getProductId().equals(productId)) {
            throw new RuntimeException("SKU不属于该商品");
        }

        // 验证价格
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("价格不能为空且不能小于0");
        }

        // 保存旧价格用于审计日志
        BigDecimal oldPrice = sku.getPrice();

        // 更新SKU价格
        sku.setPrice(price);
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateByPrimaryKeySelective(sku);

        // 记录审计日志
        recordAuditLog(operatorUserId, skuId, "sku", "update_price",
                String.format("商家[%d]更新SKU[%d]价格从%s到%s", merchantId, skuId, oldPrice, price));

        // 清除相关缓存
        Product product = productMapper.selectByPrimaryKey(productId);
        clearShopProductsCache(product.getShopId());
        clearProductCache(productId);

        // 转换为VO
        SkuVO vo = new SkuVO();
        vo.setSkuId(sku.getSkuId());
        vo.setSpec(sku.getSpec());
        vo.setPrice(sku.getPrice());
        vo.setStock(sku.getStock());
        vo.setImageUrl(sku.getImageUrl());
        return vo;
    }

    /**
     * 上架商品
     * 
     * @param productId 商品ID
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @throws RuntimeException 操作失败时抛出异常
     */
    @Transactional
    public void onShelfProduct(Long productId, Long merchantId, Long operatorUserId) {
        updateProductStatus(productId, ProductStatus.ON_SHELF, merchantId, operatorUserId, "上架");
    }

    /**
     * 下架商品
     * 
     * @param productId 商品ID
     * @param merchantId 商家ID（用于验证权限）
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @throws RuntimeException 操作失败时抛出异常
     */
    @Transactional
    public void offShelfProduct(Long productId, Long merchantId, Long operatorUserId) {
        updateProductStatus(productId, ProductStatus.OFF_SHELF, merchantId, operatorUserId, "下架");
    }

    /**
     * 更新商品状态（管理员操作，不需要验证码）
     * 
     * @param productId 商品ID
     * @param status 新状态
     * @param operatorUserId 操作者用户ID（用于审计日志）
     * @throws RuntimeException 操作失败时抛出异常
     */
    @Transactional
    public void updateProductStatusByAdmin(Long productId, Integer status, Long operatorUserId) {
        // 验证商品是否存在
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 验证状态值（管理员可以设置为 ON_SHELF、OFF_SHELF 或 ADMIN_OFF_SHELF）
        if (status != ProductStatus.ON_SHELF && status != ProductStatus.OFF_SHELF && status != ProductStatus.ADMIN_OFF_SHELF) {
            throw new RuntimeException("无效的商品状态");
        }

        // 更新商品状态
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateByPrimaryKeySelective(product);

        // 记录审计日志
        String statusName;
        if (status == ProductStatus.ON_SHELF) {
            statusName = "上架";
        } else if (status == ProductStatus.ADMIN_OFF_SHELF) {
            statusName = "管理员下架";
        } else {
            statusName = "下架";
        }
        recordAuditLog(operatorUserId, productId, "product", "update_status",
                String.format("管理员将商品[%s]%s", product.getName(), statusName));

        // 清除店铺商品列表缓存
        clearShopProductsCache(product.getShopId());
    }

    /**
     * 清除店铺商品列表缓存
     * 
     * @param shopId 店铺ID
     */
    private void clearShopProductsCache(Long shopId) {
        String keyPattern = SHOP_PRODUCTS_KEY_PREFIX + shopId + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 清除商品相关缓存（商品详情、首页、商品列表等）
     * 当SKU价格、图片、库存等信息发生变化时调用
     * 
     * @param productId 商品ID
     */
    private void clearProductCache(Long productId) {
        // 清除商品详情缓存
        String detailKey = PRODUCT_DETAIL_KEY_PREFIX + productId;
        redisTemplate.delete(detailKey);

        // 清除首页缓存（因为首页可能包含该商品）
        redisTemplate.delete(HOME_PAGE_KEY);

        // 清除所有商品列表缓存（因为价格变化可能影响列表中的最低价格）
        Set<String> keys = redisTemplate.keys(PRODUCT_LIST_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 更新商品状态（内部方法）
     */
    private void updateProductStatus(Long productId, Integer status, Long merchantId, 
                                     Long operatorUserId, String operationName) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法操作商品");
        }

        // 验证商品是否存在
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 验证店铺是否属于该商家
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null || !shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该商品");
        }

        // 验证店铺状态（只有ACTIVE状态的店铺才能操作商品）
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法操作商品");
        }
        
        // 如果商品状态是 ADMIN_OFF_SHELF，商家无法上架
        if (status == ProductStatus.ON_SHELF && product.getStatus() == ProductStatus.ADMIN_OFF_SHELF) {
            throw new RuntimeException("商品已被管理员下架，无法上架");
        }

        // 更新商品状态
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateByPrimaryKeySelective(product);

        // 记录审计日志
        recordAuditLog(operatorUserId, productId, "product", "update_status",
                String.format("商家[%d]将商品[%s]%s", merchantId, product.getName(), operationName));

        // 清除店铺商品列表缓存
        clearShopProductsCache(product.getShopId());
    }

    /**
     * 验证商品是否属于商家
     * 包含商家状态、店铺状态等完整验证
     */
    public void validateProductOwnership(Long productId, Long merchantId) {
        // 验证商家状态
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == null || merchant.getStatus() != 1) {
            throw new RuntimeException("商家已被禁用，无法操作商品");
        }

        // 验证商品是否存在
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 验证店铺是否属于该商家
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null || !shop.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该商品");
        }

        // 验证店铺状态（只有ACTIVE状态的店铺才能操作商品）
        if (shop.getStatus() == null || shop.getStatus() != ShopStatus.ACTIVE) {
            throw new RuntimeException("店铺非正常营业状态，无法操作商品");
        }
    }

    /**
     * 根据用户ID获取商家ID
     */
    private Long getMerchantIdByUserId(Long userId) {
        MerchantExample example = new MerchantExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<Merchant> merchants = merchantMapper.selectByExample(example);
        if (merchants == null || merchants.isEmpty()) {
            throw new RuntimeException("用户不是商家");
        }
        return merchants.get(0).getMerchantId();
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUsrId, Long targetId, String targetType,
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUsrId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }

    /**
     * 转换为ShopProductVO列表
     */
    private List<ShopProductVO> convertToShopProductVOList(List<Map<String, Object>> productMaps) {
        List<ShopProductVO> products = new ArrayList<>();
        for (Map<String, Object> map : productMaps) {
            products.add(convertMapToShopProductVO(map));
        }
        return products;
    }

    /**
     * 将Map转换为ShopProductVO
     */
    private ShopProductVO convertMapToShopProductVO(Map<String, Object> map) {
        ShopProductVO vo = new ShopProductVO();
        vo.setProductId(((Number) map.get("product_id")).longValue());
        vo.setName((String) map.get("name"));
        vo.setStatus(((Number) map.get("status")).intValue());
        vo.setDescription((String) map.get("description"));
        vo.setCoverImageUrl((String) map.get("cover_image_url"));
        
        Object minPriceObj = map.get("min_price");
        if (minPriceObj != null) {
            vo.setMinPrice(new BigDecimal(minPriceObj.toString()));
        } else {
            vo.setMinPrice(BigDecimal.ZERO);
        }
        
        Object salesObj = map.get("sales");
        if (salesObj != null) {
            vo.setSales(((Number) salesObj).intValue());
        } else {
            vo.setSales(0);
        }
        
        // 时间字段（处理Timestamp到LocalDateTime的转换）
        if (map.get("create_time") != null) {
            Object createTimeObj = map.get("create_time");
            if (createTimeObj instanceof java.sql.Timestamp) {
                vo.setCreateTime(((java.sql.Timestamp) createTimeObj).toLocalDateTime());
            } else if (createTimeObj instanceof LocalDateTime) {
                vo.setCreateTime((LocalDateTime) createTimeObj);
            }
        }
        if (map.get("update_time") != null) {
            Object updateTimeObj = map.get("update_time");
            if (updateTimeObj instanceof java.sql.Timestamp) {
                vo.setUpdateTime(((java.sql.Timestamp) updateTimeObj).toLocalDateTime());
            } else if (updateTimeObj instanceof LocalDateTime) {
                vo.setUpdateTime((LocalDateTime) updateTimeObj);
            }
        }
        
        return vo;
    }

    /**
     * 转换为ShopProductVO
     */
    private ShopProductVO convertToShopProductVO(Product product) {
        ShopProductVO vo = new ShopProductVO();
        vo.setProductId(product.getProductId());
        vo.setName(product.getName());
        vo.setStatus(product.getStatus());
        vo.setDescription(product.getDescription());
        vo.setCoverImageUrl(product.getImageUrl());
        vo.setMinPrice(BigDecimal.ZERO); // 新商品还没有SKU，价格为0
        vo.setSales(0); // 新商品销量为0
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        return vo;
    }
}

