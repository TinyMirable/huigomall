-- ============================================
-- Product Generation Script
-- ============================================
-- Generate 3-4 products for each third-level category for two merchants
-- Includes product information, SKU information, and product-category associations
-- 
-- Usage:
-- 1. Ensure database has been initialized (run init.sql)
-- 2. Ensure accounts have been generated (run AccountGenerator or manually create merchant1 and merchant2 accounts)
-- 3. Ensure each merchant has a shop (script will create automatically if not exists)
-- 4. Run this script: \i insert_products.sql or psql -d hgmall_db -f insert_products.sql

-- Set client encoding
SET client_encoding = 'UTF8';

-- ============================================
-- Part 1: Ensure merchants and shops exist
-- ============================================

-- Create merchant records (if not exists)
-- Assuming merchant1 and merchant2 user IDs are the first two merchant role users
DO $$
DECLARE
    merchant1_user_id BIGINT;
    merchant2_user_id BIGINT;
    merchant1_id BIGINT;
    merchant2_id BIGINT;
    shop1_id BIGINT;
    shop2_id BIGINT;
BEGIN
    -- Query user IDs for merchant1 and merchant2
    SELECT user_id INTO merchant1_user_id FROM usr WHERE user_name = 'merchant1' LIMIT 1;
    SELECT user_id INTO merchant2_user_id FROM usr WHERE user_name = 'merchant2' LIMIT 1;
    
    -- Create or get merchant1's merchant record
    IF merchant1_user_id IS NOT NULL THEN
        SELECT merchant_id INTO merchant1_id FROM merchant WHERE user_id = merchant1_user_id LIMIT 1;
        IF merchant1_id IS NULL THEN
            INSERT INTO merchant (merchant_name, owner, user_id, status, create_time, update_time)
            VALUES ('merchant1商家', 'merchant1', merchant1_user_id, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING merchant_id INTO merchant1_id;
            RAISE NOTICE 'Created merchant1: merchant_id = %', merchant1_id;
        END IF;
        
        -- Create or get shop1
        SELECT shop_id INTO shop1_id FROM shop WHERE merchant_id = merchant1_id LIMIT 1;
        IF shop1_id IS NULL THEN
            INSERT INTO shop (merchant_id, name, status, description, create_time, update_time)
            VALUES (merchant1_id, '店铺1', 1, 'merchant1的店铺', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING shop_id INTO shop1_id;
            RAISE NOTICE 'Created shop1: shop_id = %', shop1_id;
        END IF;
    END IF;
    
    -- Create or get merchant2's merchant record
    IF merchant2_user_id IS NOT NULL THEN
        SELECT merchant_id INTO merchant2_id FROM merchant WHERE user_id = merchant2_user_id LIMIT 1;
        IF merchant2_id IS NULL THEN
            INSERT INTO merchant (merchant_name, owner, user_id, status, create_time, update_time)
            VALUES ('merchant2商家', 'merchant2', merchant2_user_id, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING merchant_id INTO merchant2_id;
            RAISE NOTICE 'Created merchant2: merchant_id = %', merchant2_id;
        END IF;
        
        -- Create or get shop2
        SELECT shop_id INTO shop2_id FROM shop WHERE merchant_id = merchant2_id LIMIT 1;
        IF shop2_id IS NULL THEN
            INSERT INTO shop (merchant_id, name, status, description, create_time, update_time)
            VALUES (merchant2_id, '店铺2', 1, 'merchant2的店铺', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING shop_id INTO shop2_id;
            RAISE NOTICE 'Created shop2: shop_id = %', shop2_id;
        END IF;
    END IF;
    
    -- Store shop_id in a session variable (using a custom function or global temp table)
    -- Create global temp table that persists across DO blocks
    CREATE TEMP TABLE IF NOT EXISTS temp_shops (shop_id BIGINT);
    DELETE FROM temp_shops;
    IF shop1_id IS NOT NULL THEN INSERT INTO temp_shops VALUES (shop1_id); END IF;
    IF shop2_id IS NOT NULL THEN INSERT INTO temp_shops VALUES (shop2_id); END IF;
END $$;

-- ============================================
-- Part 2: Generate product data
-- ============================================
-- Use CTE to generate products, 3-4 products for each third-level category for each shop

-- First, verify temp_shops exists and has data
DO $$
DECLARE
    shop_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO shop_count FROM temp_shops;
    IF shop_count = 0 THEN
        RAISE EXCEPTION 'No shops found. Please ensure merchants and shops exist.';
    END IF;
    RAISE NOTICE 'Found % shops', shop_count;
END $$;

WITH 
-- Get all third-level categories (category whose parent's parent is not NULL)
level1_categories AS (
    SELECT category_id, name FROM category WHERE parent_id IS NULL
),
level2_categories AS (
    SELECT c.category_id, c.name, c.parent_id 
    FROM category c 
    JOIN level1_categories l1 ON c.parent_id = l1.category_id
),
level3_categories AS (
    SELECT c.category_id, c.name, c.parent_id
    FROM category c
    JOIN level2_categories l2 ON c.parent_id = l2.category_id
),
-- Get shop IDs
shops AS (
    SELECT shop_id FROM temp_shops
),
-- Generate product numbers (3-4 products for each third-level category for each shop)
product_numbers AS (
    SELECT 
        s.shop_id,
        l3.category_id,
        l3.name AS category_name,
        generate_series(1, CASE WHEN (s.shop_id + l3.category_id) % 2 = 0 THEN 3 ELSE 4 END) AS product_num
    FROM shops s
    CROSS JOIN level3_categories l3
)
INSERT INTO product (shop_id, name, status, description, image_url, create_time, update_time)
SELECT 
    pn.shop_id,
    pn.category_name || '商品' || pn.product_num AS name,
    1 AS status,
    '这是' || pn.category_name || '分类下的第' || pn.product_num || '个商品，品质优良，值得购买。' AS description,
    -- Use picsum.photos to provide random images, each product has a unique image
    -- Use shop_id, category_id and product_num combination to generate unique seed, ensuring each product has a different image
    'https://picsum.photos/seed/' || (pn.shop_id * 10000 + pn.category_id * 100 + pn.product_num) || '/800/800' AS image_url,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
FROM product_numbers pn
WHERE NOT EXISTS (
    -- Avoid duplicate inserts
    SELECT 1 FROM product p 
    WHERE p.shop_id = pn.shop_id 
    AND p.name = pn.category_name || '商品' || pn.product_num
);

-- ============================================
-- Part 3: Create SKU for each product
-- ============================================

INSERT INTO sku (product_id, spec, price, stock, image_url, create_time, update_time)
SELECT 
    p.product_id,
    '{"规格":"默认规格"}' AS spec,
    -- Price: random value between 50-500 (using product_id as seed to ensure consistency)
    (50 + (p.product_id % 451))::NUMERIC(10, 2) AS price,
    -- Stock: random value between 100-1000
    100 + (p.product_id % 901) AS stock,
    -- SKU image uses product image
    p.image_url,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
FROM product p
WHERE NOT EXISTS (
    SELECT 1 FROM sku WHERE sku.product_id = p.product_id AND sku.spec = '{"规格":"默认规格"}'
)
ON CONFLICT (product_id, spec) DO NOTHING;

-- ============================================
-- Part 4: Associate products with categories
-- ============================================

WITH 
-- Get all third-level categories
level1_categories AS (
    SELECT category_id, name FROM category WHERE parent_id IS NULL
),
level2_categories AS (
    SELECT c.category_id, c.name, c.parent_id 
    FROM category c 
    JOIN level1_categories l1 ON c.parent_id = l1.category_id
),
level3_categories AS (
    SELECT c.category_id, c.name, c.parent_id
    FROM category c
    JOIN level2_categories l2 ON c.parent_id = l2.category_id
),
-- Extract category name from product name (e.g., "智能手机商品1" -> "智能手机")
-- Note: This matches products with names like "CategoryName商品N"
products_with_category AS (
    SELECT 
        p.product_id,
        p.name AS product_name,
        p.shop_id,
        -- Extract category name (remove "商品" and everything after)
        CASE 
            WHEN p.name LIKE '%商品%' THEN 
                SUBSTRING(p.name FROM 1 FOR POSITION('商品' IN p.name) - 1)
            ELSE NULL
        END AS category_name
    FROM product p
    WHERE p.name LIKE '%商品%'
    AND NOT EXISTS (
        -- Only process products that haven't been associated with categories yet
        SELECT 1 FROM product_category pc WHERE pc.product_id = p.product_id
    )
)
INSERT INTO product_category (product_id, category_id)
SELECT DISTINCT
    pwc.product_id,
    l3.category_id
FROM products_with_category pwc
JOIN level3_categories l3 ON l3.name = pwc.category_name
ON CONFLICT DO NOTHING;

-- ============================================
-- Part 5: Statistics
-- ============================================

DO $$
DECLARE
    product_count INTEGER;
    sku_count INTEGER;
    category_link_count INTEGER;
    shop_count INTEGER;
    new_product_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO product_count FROM product;
    SELECT COUNT(*) INTO sku_count FROM sku;
    SELECT COUNT(*) INTO category_link_count FROM product_category;
    SELECT COUNT(*) INTO shop_count FROM temp_shops;
    
    -- Count new products created in this run (products with "商品" in name)
    SELECT COUNT(*) INTO new_product_count FROM product WHERE name LIKE '%商品%';
    
    RAISE NOTICE '==========================================';
    RAISE NOTICE 'Product generation completed!';
    RAISE NOTICE 'Shop count: %', shop_count;
    RAISE NOTICE 'Total products: %', product_count;
    RAISE NOTICE 'New products created: %', new_product_count;
    RAISE NOTICE 'Total SKUs: %', sku_count;
    RAISE NOTICE 'Product-category associations: %', category_link_count;
    RAISE NOTICE '==========================================';
END $$;

-- Clean up temporary table
DROP TABLE IF EXISTS temp_shops;

-- ============================================
-- Complete
-- ============================================
