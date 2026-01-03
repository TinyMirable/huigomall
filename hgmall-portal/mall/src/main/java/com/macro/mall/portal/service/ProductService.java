package com.macro.mall.portal.service;

import com.macro.mall.common.domain.*;
import com.macro.mall.common.domain.SkuVO;
import com.macro.mall.mapper.CategoryMapper;
import com.macro.mall.mapper.MerchantMapper;
import com.macro.mall.mapper.ProductMapper;
import com.macro.mall.common.constant.ProductStatus;
import com.macro.mall.common.constant.ShopStatus;
import com.macro.mall.mapper.ShopMapper;
import com.macro.mall.mapper.SkuMapper;
import com.macro.mall.model.Category;
import com.macro.mall.model.CategoryExample;
import com.macro.mall.model.Merchant;
import com.macro.mall.model.Product;
import com.macro.mall.model.Shop;
import com.macro.mall.model.Sku;
import com.macro.mall.model.SkuExample;
import com.macro.mall.common.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品服务
 * 提供商品查询、缓存等功能
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String HOME_PAGE_KEY = "home:page";
    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";
    private static final String PRODUCT_DETAIL_KEY_PREFIX = "product:detail:";
    private static final String PRODUCT_SALES_RANK_KEY = "product:sales:rank";

    /**
     * 缓存TTL（秒）
     */
    private static final long HOME_PAGE_TTL = 60; // 30-120秒，这里取60秒
    private static final long PRODUCT_LIST_TTL = 45; // 30-60秒，这里取45秒
    private static final long PRODUCT_DETAIL_TTL = 300; // 5分钟

    /**
     * 首页数据
     * 返回每个一级分类下最新16个商品 + 全站销量Top3商品
     */
    public HomePageVO getHomePage() {
        // 尝试从缓存获取
        HomePageVO cached = (HomePageVO) redisTemplate.opsForValue().get(HOME_PAGE_KEY);
        if (cached != null) {
            return cached;
        }

        // 查询所有一级分类（parent_id为null）
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andParentIdIsNull();
        categoryExample.setOrderByClause("sort_order ASC, category_id ASC");
        List<Category> topCategories = categoryMapper.selectByExample(categoryExample);

        // 查询每个一级分类下最新16个商品（递归查询子分类）
        List<CategoryProductsVO> categoryProducts = new ArrayList<>();
        for (Category category : topCategories) {
            // 递归获取该分类及其所有子分类的ID列表
            List<Long> allCategoryIds = categoryService.getCategoryIdsWithChildren(category.getCategoryId());
            
            // 使用支持多分类查询的方法，查询最新16个商品
            List<Map<String, Object>> productMaps = productMapper.selectByCategoriesWithPaging(allCategoryIds, 0, 16);
            if (!productMaps.isEmpty()) {
                List<ProductVO> products = convertToProductVOList(productMaps);
                CategoryProductsVO categoryProductsVO = new CategoryProductsVO();
                categoryProductsVO.setCategoryId(category.getCategoryId());
                categoryProductsVO.setCategoryName(category.getName());
                categoryProductsVO.setProducts(products);
                categoryProducts.add(categoryProductsVO);
            }
        }

        // 查询全站销量Top3商品（优先从Redis ZSet获取）
        List<ProductVO> topSalesProducts = getTopSalesProductsFromRedis(3);
        if (topSalesProducts.isEmpty()) {
            // 如果Redis中没有，从数据库查询
            List<Map<String, Object>> productMaps = productMapper.selectTopSales(3);
            topSalesProducts = convertToProductVOList(productMaps);
            // 同步到Redis ZSet
            syncTopSalesToRedis(topSalesProducts);
        }

        // 构建返回结果
        HomePageVO homePageVO = new HomePageVO();
        homePageVO.setCategoryProducts(categoryProducts);
        homePageVO.setTopSalesProducts(topSalesProducts);

        // 存入缓存（随机TTL，30-120秒）
        long ttl = HOME_PAGE_TTL + new Random().nextInt(90);
        redisTemplate.opsForValue().set(HOME_PAGE_KEY, homePageVO, ttl, TimeUnit.SECONDS);

        return homePageVO;
    }

    /**
     * 分类商品列表（分页）
     * 支持单个或多个分类筛选，自动递归查询子分类
     * 
     * @param categoryIds 分类ID列表（可为null或空，表示查询所有商品）
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    public ProductListVO getProductList(List<Long> categoryIds, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        // 递归获取所有子分类ID（包含自身）
        List<Long> allCategoryIds = categoryService.getCategoryIdsWithChildren(categoryIds);

        // 构建缓存Key（对分类ID列表排序后拼接，确保相同分类组合的缓存Key一致）
        String categoryKey;
        if (allCategoryIds == null || allCategoryIds.isEmpty()) {
            categoryKey = "all";
        } else {
            // 排序并去重，确保相同分类组合生成相同的缓存Key
            List<Long> sortedIds = new ArrayList<>(new java.util.TreeSet<>(allCategoryIds));
            categoryKey = sortedIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
        String cacheKey = PRODUCT_LIST_KEY_PREFIX + categoryKey 
                + ":page=" + page + ":size=" + size;

        // 尝试从缓存获取
        ProductListVO cached = (ProductListVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询商品列表（使用包含子分类的ID列表）
        List<Map<String, Object>> productMaps = productMapper.selectByCategoriesWithPaging(allCategoryIds, offset, size);
        List<ProductVO> products = convertToProductVOList(productMaps);

        // 查询总数（使用包含子分类的ID列表）
        Long total = productMapper.countByCategories(allCategoryIds);

        // 构建返回结果
        ProductListVO result = new ProductListVO();
        result.setProducts(products);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        // 存入缓存（随机TTL，30-60秒）
        long ttl = PRODUCT_LIST_TTL + new Random().nextInt(30);
        redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 商品详情
     */
    public ProductDetailVO getProductDetail(Long productId) {
        // 构建缓存Key
        String cacheKey = PRODUCT_DETAIL_KEY_PREFIX + productId;

        // 尝试从缓存获取
        ProductDetailVO cached = (ProductDetailVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询商品信息
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return null;
        }

        // 查询店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null) {
            return null;
        }

        // 商品详情页允许只读访问（即使店铺非 ACTIVE 状态）
        // 但只有 ACTIVE 店铺且商品上架的商品才能正常购买
        // 这里只检查商品和店铺是否存在，不强制要求店铺 ACTIVE 或商品上架

        // 查询SKU列表
        SkuExample skuExample = new SkuExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        List<Sku> skus = skuMapper.selectByExample(skuExample);

        // 计算销量（从order_item统计）
        Integer sales = calculateProductSales(productId);

        // 查询商家名称
        String merchantName = getMerchantNameByProductId(productId);

        // 构建SKU VO列表
        List<SkuVO> skuVOList = skus.stream()
                .map(sku -> {
                    SkuVO skuVO = new SkuVO();
                    skuVO.setSkuId(sku.getSkuId());
                    skuVO.setSpec(sku.getSpec());
                    skuVO.setPrice(sku.getPrice());
                    skuVO.setStock(sku.getStock());
                    skuVO.setImageUrl(sku.getImageUrl());
                    return skuVO;
                })
                .collect(Collectors.toList());

        // 构建返回结果
        ProductDetailVO result = new ProductDetailVO();
        result.setProductId(product.getProductId());
        result.setName(product.getName());
        result.setDescription(product.getDescription());
        result.setCoverImageUrl(product.getImageUrl());
        result.setSales(sales);
        result.setSkus(skuVOList);
        result.setMerchantName(merchantName);
        result.setShopId(product.getShopId());

        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, PRODUCT_DETAIL_TTL, TimeUnit.SECONDS);

        return result;
    }

    /**
     * 计算商品销量（从order_item统计）
     */
    private Integer calculateProductSales(Long productId) {
        // 优先从Redis ZSet获取销量（更快速）
        String productIdStr = String.valueOf(productId);
        Double score = redisTemplate.opsForZSet().score(PRODUCT_SALES_RANK_KEY, productIdStr);
        if (score != null) {
            return score.intValue();
        }

        // 如果Redis中没有，从数据库查询
        Integer sales = productMapper.getProductSales(productId);
        if (sales == null) {
            sales = 0;
        }

        // 同步到Redis ZSet
        redisTemplate.opsForZSet().add(PRODUCT_SALES_RANK_KEY, productIdStr, sales);

        return sales;
    }

    /**
     * 从Redis ZSet获取销量Top N商品
     */
    private List<ProductVO> getTopSalesProductsFromRedis(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(PRODUCT_SALES_RANK_KEY, 0, limit - 1);

        if (tuples == null || tuples.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductVO> products = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            Long productId = Long.parseLong(tuple.getValue().toString());
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product != null && product.getStatus() != null && product.getStatus() == ProductStatus.ON_SHELF) {
                // 检查店铺状态
                Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
                if (shop != null && shop.getStatus() != null && shop.getStatus() == ShopStatus.ACTIVE) {
                    ProductVO productVO = buildProductVO(product, tuple.getScore().intValue());
                    products.add(productVO);
                }
            }
        }

        return products;
    }

    /**
     * 同步销量Top商品到Redis ZSet
     */
    private void syncTopSalesToRedis(List<ProductVO> products) {
        for (ProductVO product : products) {
            redisTemplate.opsForZSet().add(
                    PRODUCT_SALES_RANK_KEY,
                    String.valueOf(product.getProductId()),
                    product.getSales() != null ? product.getSales() : 0
            );
        }
    }

    /**
     * 将Map列表转换为ProductVO列表
     */
    private List<ProductVO> convertToProductVOList(List<Map<String, Object>> productMaps) {
        List<ProductVO> products = new ArrayList<>();
        for (Map<String, Object> map : productMaps) {
            ProductVO productVO = new ProductVO();
            productVO.setProductId(((Number) map.get("product_id")).longValue());
            productVO.setName((String) map.get("name"));
            productVO.setCoverImageUrl((String) map.get("cover_image_url"));
            Object minPriceObj = map.get("min_price");
            if (minPriceObj != null) {
                productVO.setMinPrice(new BigDecimal(minPriceObj.toString()));
            } else {
                productVO.setMinPrice(BigDecimal.ZERO);
            }
            Object salesObj = map.get("sales");
            if (salesObj != null) {
                productVO.setSales(((Number) salesObj).intValue());
            } else {
                productVO.setSales(0);
            }
            productVO.setMerchantName((String) map.get("merchant_name"));
            Object shopIdObj = map.get("shop_id");
            if (shopIdObj != null) {
                productVO.setShopId(((Number) shopIdObj).longValue());
            }
            products.add(productVO);
        }
        return products;
    }

    /**
     * 构建ProductVO
     */
    private ProductVO buildProductVO(Product product, Integer sales) {
        // 查询最低SKU价格
        SkuExample skuExample = new SkuExample();
        skuExample.createCriteria().andProductIdEqualTo(product.getProductId());
        List<Sku> skus = skuMapper.selectByExample(skuExample);
        BigDecimal minPrice = skus.stream()
                .map(Sku::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 查询商家名称
        String merchantName = getMerchantNameByProductId(product.getProductId());

        ProductVO productVO = new ProductVO();
        productVO.setProductId(product.getProductId());
        productVO.setName(product.getName());
        productVO.setCoverImageUrl(product.getImageUrl());
        productVO.setMinPrice(minPrice);
        productVO.setSales(sales);
        productVO.setMerchantName(merchantName);
        productVO.setShopId(product.getShopId());
        return productVO;
    }

    /**
     * 根据商品ID查询商家名称
     */
    private String getMerchantNameByProductId(Long productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getShopId() == null) {
            return null;
        }
        Shop shop = shopMapper.selectByPrimaryKey(product.getShopId());
        if (shop == null || shop.getMerchantId() == null) {
            return null;
        }
        Merchant merchant = merchantMapper.selectByPrimaryKey(shop.getMerchantId());
        if (merchant == null) {
            return null;
        }
        return merchant.getMerchantName();
    }

    /**
     * 更新商品销量（下单成功时调用）
     * 同步更新MySQL和Redis
     */
    public void updateProductSales(Long productId, Integer quantity) {
        // 更新Redis ZSet
        redisTemplate.opsForZSet().incrementScore(
                PRODUCT_SALES_RANK_KEY,
                String.valueOf(productId),
                quantity
        );

        // 清除相关缓存
        clearProductCache(productId);
    }

    /**
     * 清除商品相关缓存
     */
    private void clearProductCache(Long productId) {
        // 清除商品详情缓存
        String detailKey = PRODUCT_DETAIL_KEY_PREFIX + productId;
        redisTemplate.delete(detailKey);

        // 清除首页缓存
        redisTemplate.delete(HOME_PAGE_KEY);

        // 清除所有商品列表缓存（可以优化为只清除相关分类的缓存）
        Set<String> keys = redisTemplate.keys(PRODUCT_LIST_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

