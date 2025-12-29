package com.macro.mall;

import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 商品生成器
 * 为两个商家在每个三级分类下生成3-4个商品
 * 
 * 使用方法：
 * 1. 确保数据库已经初始化（执行了 init.sql）
 * 2. 确保已经生成了账号（执行了 AccountGenerator）
 * 3. 确保每个商家都已经创建了店铺
 * 4. 确保 generator.properties 中的数据库配置正确
 * 5. 运行此类的主方法
 */
public class ProductGenerator {

    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("================== 开始生成商品 ==================");
        
        SqlSession sqlSession = null;
        try {
            // 1. 读取数据库配置
            Properties props = new Properties();
            InputStream propsStream = ProductGenerator.class.getClassLoader()
                    .getResourceAsStream("generator.properties");
            if (propsStream == null) {
                System.err.println("错误：无法找到配置文件 generator.properties");
                return;
            }
            props.load(propsStream);
            propsStream.close();

            String driver = props.getProperty("jdbc.driverClass");
            String url = props.getProperty("jdbc.connectionURL");
            String username = props.getProperty("jdbc.userId");
            String password = props.getProperty("jdbc.password");

            // 2. 创建 MyBatis SqlSessionFactory
            String resource = "mybatis-config.xml";
            InputStream inputStream = ProductGenerator.class.getClassLoader()
                    .getResourceAsStream(resource);
            
            SqlSessionFactory sqlSessionFactory;
            if (inputStream != null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
                inputStream.close();
            } else {
                // 如果没有配置文件，使用代码方式配置
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                configuration.setEnvironment(new org.apache.ibatis.mapping.Environment(
                    "development",
                    new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(),
                    new org.apache.ibatis.datasource.pooled.PooledDataSource(driver, url, username, password)
                ));
                
                // 手动添加Mapper接口
                configuration.addMapper(CategoryMapper.class);
                configuration.addMapper(MerchantMapper.class);
                configuration.addMapper(ShopMapper.class);
                configuration.addMapper(ProductMapper.class);
                configuration.addMapper(SkuMapper.class);
                configuration.addMapper(ProductCategoryMapper.class);
                
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            }

            // 3. 创建 SqlSession
            sqlSession = sqlSessionFactory.openSession();

            // 4. 获取 Mapper
            UsrMapper usrMapper = sqlSession.getMapper(UsrMapper.class);
            CategoryMapper categoryMapper = sqlSession.getMapper(CategoryMapper.class);
            MerchantMapper merchantMapper = sqlSession.getMapper(MerchantMapper.class);
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            SkuMapper skuMapper = sqlSession.getMapper(SkuMapper.class);
            ProductCategoryMapper productCategoryMapper = sqlSession.getMapper(ProductCategoryMapper.class);

            // 5. 生成商品
            generateProducts(usrMapper, categoryMapper, merchantMapper, shopMapper, productMapper, skuMapper, productCategoryMapper);

            // 6. 提交事务
            sqlSession.commit();
            
            System.out.println("\n================== 商品生成完成 ==================");
            
        } catch (Exception e) {
            System.err.println("异常：" + e.getMessage());
            e.printStackTrace();
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 生成商品
     */
    private static void generateProducts(UsrMapper usrMapper, CategoryMapper categoryMapper, 
                                       MerchantMapper merchantMapper, ShopMapper shopMapper, 
                                       ProductMapper productMapper, SkuMapper skuMapper, 
                                       ProductCategoryMapper productCategoryMapper) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 查询两个商家（通过用户名找到用户，再找到商家）
        List<Merchant> merchants = getMerchants(usrMapper, merchantMapper);
        if (merchants.size() < 2) {
            System.err.println("错误：商家数量不足，需要至少2个商家。请先运行 AccountGenerator 生成商家账号，并创建商家记录。");
            return;
        }

        // 2. 查询每个商家的店铺（如果没有则创建）
        List<Shop> shops = new ArrayList<>();
        for (Merchant merchant : merchants) {
            Shop shop = getOrCreateShop(shopMapper, merchant.getMerchantId(), now);
            if (shop != null) {
                shops.add(shop);
            }
        }

        if (shops.isEmpty()) {
            System.err.println("错误：没有找到或创建店铺。");
            return;
        }

        // 3. 查询所有三级分类
        List<Category> level3Categories = getLevel3Categories(categoryMapper);
        System.out.println("\n找到 " + level3Categories.size() + " 个三级分类");

        // 4. 为每个商家的店铺在每个三级分类下生成3-4个商品
        int totalProducts = 0;
        for (Shop shop : shops) {
            System.out.println("\n--- 为店铺 \"" + shop.getName() + "\" (ID: " + shop.getShopId() + ") 生成商品 ---");
            
            for (Category category : level3Categories) {
                int productCount = 3 + random.nextInt(2); // 3-4个商品
                System.out.println("  分类: " + category.getName() + " (ID: " + category.getCategoryId() + ") - 生成 " + productCount + " 个商品");
                
                for (int i = 1; i <= productCount; i++) {
                    Product product = createProduct(productMapper, shop.getShopId(), 
                        category.getName(), i, now);
                    
                    if (product != null) {
                        // 创建SKU
                        createSku(skuMapper, product.getProductId(), now);
                        
                        // 关联分类
                        linkProductToCategory(productCategoryMapper, product.getProductId(), category.getCategoryId());
                        
                        totalProducts++;
                    }
                }
            }
        }

        System.out.println("\n总共生成了 " + totalProducts + " 个商品");
    }

    /**
     * 获取商家列表（通过用户名merchant1和merchant2）
     */
    private static List<Merchant> getMerchants(UsrMapper usrMapper, MerchantMapper merchantMapper) {
        List<Merchant> merchants = new ArrayList<>();
        
        // 查询merchant1和merchant2用户
        String[] merchantUserNames = {"merchant1", "merchant2"};
        for (String userName : merchantUserNames) {
            try {
                // 查询用户
                com.macro.mall.model.UsrExample userExample = new com.macro.mall.model.UsrExample();
                userExample.createCriteria().andUserNameEqualTo(userName);
                List<com.macro.mall.model.Usr> users = usrMapper.selectByExample(userExample);
                
                if (users.isEmpty()) {
                    System.err.println("  警告：用户 " + userName + " 不存在");
                    continue;
                }
                
                com.macro.mall.model.Usr user = users.get(0);
                
                // 查询该用户对应的商家
                MerchantExample merchantExample = new MerchantExample();
                merchantExample.createCriteria().andUserIdEqualTo(user.getUserId());
                List<Merchant> foundMerchants = merchantMapper.selectByExample(merchantExample);
                
                if (foundMerchants.isEmpty()) {
                    System.err.println("  警告：用户 " + userName + " 还没有创建商家记录，正在创建...");
                    // 创建商家记录
                    Merchant merchant = new Merchant();
                    merchant.setUserId(user.getUserId());
                    merchant.setMerchantName(userName + "商家");
                    merchant.setOwner(userName);
                    merchant.setStatus(1);
                    merchant.setCreateTime(LocalDateTime.now());
                    merchant.setUpdateTime(LocalDateTime.now());
                    
                    int result = merchantMapper.insertSelective(merchant);
                    if (result > 0) {
                        merchants.add(merchant);
                        System.out.println("  已创建商家记录: " + merchant.getMerchantName() + " (ID: " + merchant.getMerchantId() + ")");
                    }
                } else {
                    merchants.add(foundMerchants.get(0));
                }
            } catch (Exception e) {
                System.err.println("  查询商家失败 (" + userName + "): " + e.getMessage());
            }
        }
        
        return merchants;
    }

    /**
     * 获取或创建店铺
     */
    private static Shop getOrCreateShop(ShopMapper shopMapper, Long merchantId, LocalDateTime now) {
        // 查询该商家是否已有店铺
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andMerchantIdEqualTo(merchantId);
        List<Shop> existingShops = shopMapper.selectByExample(shopExample);
        
        if (!existingShops.isEmpty()) {
            return existingShops.get(0);
        }
        
        // 如果没有店铺，创建一个
        Shop shop = new Shop();
        shop.setMerchantId(merchantId);
        shop.setName("店铺" + merchantId);
        shop.setDescription("测试店铺");
        shop.setStatus(1);
        shop.setCreateTime(now);
        shop.setUpdateTime(now);
        
        int result = shopMapper.insertSelective(shop);
        if (result > 0) {
            System.out.println("  创建店铺: " + shop.getName() + " (ID: " + shop.getShopId() + ")");
            return shop;
        }
        
        return null;
    }

    /**
     * 获取所有三级分类
     */
    private static List<Category> getLevel3Categories(CategoryMapper categoryMapper) {
        // 查询所有分类
        List<Category> allCategories = categoryMapper.selectByExample(null);
        List<Category> level3Categories = new ArrayList<>();
        
        // 三级分类：父分类不是null，且父分类的父分类也不是null
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : allCategories) {
            categoryMap.put(category.getCategoryId(), category);
        }
        
        for (Category category : allCategories) {
            if (category.getParentId() != null) {
                Category parent = categoryMap.get(category.getParentId());
                if (parent != null && parent.getParentId() != null) {
                    // 这是三级分类
                    level3Categories.add(category);
                }
            }
        }
        
        return level3Categories;
    }

    /**
     * 创建商品
     */
    private static Product createProduct(ProductMapper productMapper, Long shopId, 
                                       String categoryName, int index, LocalDateTime now) {
        try {
            Product product = new Product();
            product.setShopId(shopId);
            product.setName(categoryName + "商品" + index);
            product.setDescription("这是" + categoryName + "分类下的第" + index + "个商品");
            product.setStatus(1); // 上架
            product.setImageUrl(null);
            product.setCreateTime(now);
            product.setUpdateTime(now);
            
            int result = productMapper.insertSelective(product);
            if (result > 0) {
                return product;
            }
        } catch (Exception e) {
            System.err.println("  创建商品失败: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * 创建SKU
     */
    private static void createSku(SkuMapper skuMapper, Long productId, LocalDateTime now) {
        try {
            Sku sku = new Sku();
            sku.setProductId(productId);
            sku.setSpec("{\"规格\":\"默认\"}"); // JSON格式的规格
            sku.setPrice(new BigDecimal(50 + random.nextInt(450))); // 50-500之间的随机价格
            sku.setStock(100 + random.nextInt(900)); // 100-1000之间的随机库存
            sku.setImageUrl(null);
            sku.setCreateTime(now);
            sku.setUpdateTime(now);
            
            skuMapper.insertSelective(sku);
        } catch (Exception e) {
            System.err.println("  创建SKU失败: " + e.getMessage());
        }
    }

    /**
     * 关联商品到分类
     */
    private static void linkProductToCategory(ProductCategoryMapper productCategoryMapper, 
                                            Long productId, Long categoryId) {
        try {
            ProductCategoryKey key = new ProductCategoryKey();
            key.setProductId(productId);
            key.setCategoryId(categoryId);
            
            productCategoryMapper.insertSelective(key);
        } catch (Exception e) {
            // 忽略重复关联的错误
            if (!e.getMessage().contains("duplicate") && !e.getMessage().contains("UNIQUE")) {
                System.err.println("  关联商品分类失败: " + e.getMessage());
            }
        }
    }
}

