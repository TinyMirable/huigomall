-- ============================================
-- TheMall 数据库完整初始化脚本 (PostgreSQL)
-- ============================================
-- 功能：创建数据表、索引、约束、初始化角色、创建基础分类
-- 注意：此脚本不包含管理员账号的插入，管理员账号请单独创建
-- 
-- 使用方法：
-- 1. 先创建数据库：CREATE DATABASE hgmall_db;
-- 2. 连接数据库：\c hgmall_db
-- 3. 执行此脚本：\i inpsql -d hgmall_db -f init.sql

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- ============================================
-- 第一部分：创建数据表
-- ============================================

-- 1. 角色表 (role)
CREATE TABLE IF NOT EXISTS role (
    role_id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100),
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE role IS '角色表';
COMMENT ON COLUMN role.role_id IS '角色ID';
COMMENT ON COLUMN role.role_code IS '角色编码';
COMMENT ON COLUMN role.role_name IS '角色名称';
COMMENT ON COLUMN role.description IS '角色描述';

-- 2. 用户表 (user)
CREATE TABLE IF NOT EXISTS "usr" (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id)
);

COMMENT ON TABLE "usr" IS '用户表';
COMMENT ON COLUMN "usr".user_id IS '用户ID';
COMMENT ON COLUMN "usr".user_name IS '用户名';
COMMENT ON COLUMN "usr".email IS '邮箱';
COMMENT ON COLUMN "usr".phone_number IS '手机号';
COMMENT ON COLUMN "usr".password IS '密码（加密）';
COMMENT ON COLUMN "usr".role_id IS '角色ID';
COMMENT ON COLUMN "usr".status IS '状态：1-正常，0-禁用';

-- 3. 地址表 (address)
CREATE TABLE IF NOT EXISTS address (
    address_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    detail TEXT NOT NULL,
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    is_default BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES "usr"(user_id) ON DELETE CASCADE
);

COMMENT ON TABLE address IS '地址表';
COMMENT ON COLUMN address.address_id IS '地址ID';
COMMENT ON COLUMN address.user_id IS '用户ID';
COMMENT ON COLUMN address.detail IS '详细地址';
COMMENT ON COLUMN address.receiver_name IS '收货人姓名';
COMMENT ON COLUMN address.receiver_phone IS '收货人电话';
COMMENT ON COLUMN address.is_default IS '是否默认地址';

-- 4. 商家表 (merchant)
CREATE TABLE IF NOT EXISTS merchant (
    merchant_id BIGSERIAL PRIMARY KEY,
    merchant_name VARCHAR(100) NOT NULL,
    owner VARCHAR(50),
    user_id BIGINT NOT NULL UNIQUE,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_merchant_user FOREIGN KEY (user_id) REFERENCES "usr"(user_id)
);

COMMENT ON TABLE merchant IS '商家表';
COMMENT ON COLUMN merchant.merchant_id IS '商家ID';
COMMENT ON COLUMN merchant.merchant_name IS '商家名称';
COMMENT ON COLUMN merchant.owner IS '负责人';
COMMENT ON COLUMN merchant.user_id IS '用户ID（注册商家）';
COMMENT ON COLUMN merchant.status IS '状态：1-正常，2-商家禁用，3-管理员强制禁用 ';

-- 5. 店铺表 (shop)
CREATE TABLE IF NOT EXISTS shop (
    shop_id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    status INTEGER DEFAULT 1,
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shop_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE
);

COMMENT ON TABLE shop IS '店铺表';
COMMENT ON COLUMN shop.shop_id IS '店铺ID';
COMMENT ON COLUMN shop.merchant_id IS '商家ID';
COMMENT ON COLUMN shop.name IS '店铺名称';
COMMENT ON COLUMN shop.status IS '状态：1-正常营业，2-商家关闭，3-管理员强制关闭';
COMMENT ON COLUMN shop.description IS '店铺描述';

-- 6. 商品表 (product)
CREATE TABLE IF NOT EXISTS product (
    product_id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    status INTEGER DEFAULT 1,
    description TEXT,
    image_url TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_shop FOREIGN KEY (shop_id) REFERENCES shop(shop_id) ON DELETE CASCADE
);

COMMENT ON TABLE product IS '商品表';
COMMENT ON COLUMN product.product_id IS '商品ID';
COMMENT ON COLUMN product.shop_id IS '店铺ID';
COMMENT ON COLUMN product.name IS '商品名称';
COMMENT ON COLUMN product.status IS '状态：1-上架，0-下架';
COMMENT ON COLUMN product.description IS '商品描述';
COMMENT ON COLUMN product.image_url IS '商品主图URL';

-- 7. SKU表 (sku)
CREATE TABLE IF NOT EXISTS sku (
    sku_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    spec TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    stock INTEGER DEFAULT 0,
    image_url TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sku_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    CONSTRAINT chk_sku_price CHECK (price >= 0),
    CONSTRAINT chk_sku_stock CHECK (stock >= 0)
);

COMMENT ON TABLE sku IS 'SKU表（库存量单位）';
COMMENT ON COLUMN sku.sku_id IS 'SKU ID';
COMMENT ON COLUMN sku.product_id IS '商品ID';
COMMENT ON COLUMN sku.spec IS '规格（JSON格式）';
COMMENT ON COLUMN sku.price IS '价格';
COMMENT ON COLUMN sku.stock IS '库存';
COMMENT ON COLUMN sku.image_url IS 'SKU图片URL';

-- 8. 分类表 (category)
CREATE TABLE IF NOT EXISTS category (
    category_id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(100) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(category_id) ON DELETE SET NULL
);

COMMENT ON TABLE category IS '分类表';
COMMENT ON COLUMN category.category_id IS '分类ID';
COMMENT ON COLUMN category.parent_id IS '父分类ID';
COMMENT ON COLUMN category.name IS '分类名称';
COMMENT ON COLUMN category.sort_order IS '排序序号';

-- 9. 商品分类关联表 (product_category)
CREATE TABLE IF NOT EXISTS product_category (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);

COMMENT ON TABLE product_category IS '商品分类关联表';
COMMENT ON COLUMN product_category.product_id IS '商品ID';
COMMENT ON COLUMN product_category.category_id IS '分类ID';

-- 10. 购物车表 (cart_item)
CREATE TABLE IF NOT EXISTS cart_item (
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    num INTEGER NOT NULL DEFAULT 1,
    role_code VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, item_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES "usr"(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_sku FOREIGN KEY (sku_id) REFERENCES sku(sku_id) ON DELETE CASCADE,
    CONSTRAINT chk_cart_num CHECK (num > 0)
);

COMMENT ON TABLE cart_item IS '购物车表';
COMMENT ON COLUMN cart_item.user_id IS '用户ID';
COMMENT ON COLUMN cart_item.item_id IS '购物车项ID';
COMMENT ON COLUMN cart_item.sku_id IS 'SKU ID';
COMMENT ON COLUMN cart_item.num IS '数量';
COMMENT ON COLUMN cart_item.role_code IS '角色编码';

-- 11. 批量订单表 (batch_order)
CREATE TABLE IF NOT EXISTS batch_order (
    batch_order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status INTEGER NOT NULL DEFAULT 0,
    address_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pay_time TIMESTAMP,
    expire_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_order_user FOREIGN KEY (user_id) REFERENCES "usr"(user_id),
    CONSTRAINT fk_batch_order_address FOREIGN KEY (address_id) REFERENCES address(address_id),
    CONSTRAINT chk_batch_order_total CHECK (total_amount >= 0)
);

COMMENT ON TABLE batch_order IS '批量订单表（大订单）';
COMMENT ON COLUMN batch_order.batch_order_id IS '批量订单ID';
COMMENT ON COLUMN batch_order.user_id IS '用户ID';
COMMENT ON COLUMN batch_order.total_amount IS '总金额（所有小订单金额之和）';
COMMENT ON COLUMN batch_order.status IS '订单状态：0-待支付，1-已支付，2-已取消，3-已退款';
COMMENT ON COLUMN batch_order.address_id IS '收货地址ID（订单创建时的地址快照）';
COMMENT ON COLUMN batch_order.create_time IS '创建时间';
COMMENT ON COLUMN batch_order.pay_time IS '支付时间';
COMMENT ON COLUMN batch_order.expire_time IS '过期时间（用于倒计时，默认30分钟后过期）';
COMMENT ON COLUMN batch_order.update_time IS '更新时间';

-- 12. 订单表 (order) - 小订单
CREATE TABLE IF NOT EXISTS "order" (
    order_id BIGSERIAL PRIMARY KEY,
    batch_order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    status INTEGER NOT NULL DEFAULT 0,
    total NUMERIC(10, 2) NOT NULL,
    address_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_batch_order FOREIGN KEY (batch_order_id) REFERENCES batch_order(batch_order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "usr"(user_id),
    CONSTRAINT fk_order_shop FOREIGN KEY (shop_id) REFERENCES shop(shop_id),
    CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES address(address_id),
    CONSTRAINT chk_order_total CHECK (total >= 0)
);

COMMENT ON TABLE "order" IS '订单表（小订单，按店铺分组）';
COMMENT ON COLUMN "order".order_id IS '订单ID';
COMMENT ON COLUMN "order".batch_order_id IS '批量订单ID（关联大订单）';
COMMENT ON COLUMN "order".user_id IS '用户ID';
COMMENT ON COLUMN "order".shop_id IS '店铺ID';
COMMENT ON COLUMN "order".status IS '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款';
COMMENT ON COLUMN "order".total IS '订单总金额';
COMMENT ON COLUMN "order".address_id IS '收货地址ID（订单创建时的地址快照）';

-- 13. 订单项表 (order_item)
CREATE TABLE IF NOT EXISTS order_item (
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    price_snapshot NUMERIC(10, 2) NOT NULL,
    num INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, item_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES "order"(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_sku FOREIGN KEY (sku_id) REFERENCES sku(sku_id),
    CONSTRAINT chk_order_item_price CHECK (price_snapshot >= 0),
    CONSTRAINT chk_order_item_num CHECK (num > 0)
);

COMMENT ON TABLE order_item IS '订单项表';
COMMENT ON COLUMN order_item.order_id IS '订单ID';
COMMENT ON COLUMN order_item.item_id IS '订单项ID';
COMMENT ON COLUMN order_item.sku_id IS 'SKU ID';
COMMENT ON COLUMN order_item.price_snapshot IS '快照价格（下单时的价格）';
COMMENT ON COLUMN order_item.num IS '数量';

-- 14. 审计日志表 (audit_log)
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    admin_user_id BIGINT,
    target_id BIGINT,
    target_type VARCHAR(50),
    operation_type VARCHAR(50),
    operation_desc TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_admin FOREIGN KEY (admin_user_id) REFERENCES "usr"(user_id) ON DELETE SET NULL
);

COMMENT ON TABLE audit_log IS '审计日志表';
COMMENT ON COLUMN audit_log.audit_id IS '审计日志ID';
COMMENT ON COLUMN audit_log.admin_user_id IS '管理员用户ID';
COMMENT ON COLUMN audit_log.target_id IS '目标对象ID';
COMMENT ON COLUMN audit_log.target_type IS '目标对象类型（merchant/shop/product等）';
COMMENT ON COLUMN audit_log.operation_type IS '操作类型（create/update/delete等）';
COMMENT ON COLUMN audit_log.operation_desc IS '操作描述';

-- ============================================
-- 第二部分：创建约束
-- ============================================

-- 角色表唯一约束：角色名称必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'role'::regclass 
        AND conname = 'uk_role_name'
    ) THEN
        ALTER TABLE role 
        ADD CONSTRAINT uk_role_name 
        UNIQUE (role_name);
    END IF;
END $$;

-- 用户表唯一约束：邮箱必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'usr'::regclass 
        AND conname = 'uk_user_email'
    ) THEN
        ALTER TABLE "usr" 
        ADD CONSTRAINT uk_user_email 
        UNIQUE (email);
    END IF;
END $$;

-- 用户表唯一约束：手机号必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'usr'::regclass 
        AND conname = 'uk_user_phone'
    ) THEN
        ALTER TABLE "usr" 
        ADD CONSTRAINT uk_user_phone 
        UNIQUE (phone_number);
    END IF;
END $$;

-- 地址表唯一约束：同一用户下只能有一个默认地址
-- 使用部分唯一索引实现：只有当is_default=true时才要求唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE tablename = 'address' 
        AND indexname = 'uk_address_user_default'
    ) THEN
        CREATE UNIQUE INDEX uk_address_user_default 
        ON address(user_id) 
        WHERE is_default = TRUE;
    END IF;
END $$;

-- 商家表唯一约束：商家名称必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'merchant'::regclass 
        AND conname = 'uk_merchant_name'
    ) THEN
        ALTER TABLE merchant 
        ADD CONSTRAINT uk_merchant_name 
        UNIQUE (merchant_name);
    END IF;
END $$;

-- 店铺表唯一约束：店铺名称必须全局唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'shop'::regclass 
        AND conname = 'uk_shop_name'
    ) THEN
        ALTER TABLE shop 
        ADD CONSTRAINT uk_shop_name 
        UNIQUE (name);
    END IF;
END $$;

-- SKU表唯一约束：同一商品下，规格必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'sku'::regclass 
        AND conname = 'uk_sku_product_spec'
    ) THEN
        ALTER TABLE sku 
        ADD CONSTRAINT uk_sku_product_spec 
        UNIQUE (product_id, spec);
    END IF;
END $$;

-- 分类表唯一约束：同一父分类下，分类名称必须唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'category'::regclass 
        AND conname = 'uk_category_parent_name'
    ) THEN
        ALTER TABLE category 
        ADD CONSTRAINT uk_category_parent_name 
        UNIQUE (parent_id, name);
    END IF;
END $$;

-- 分类表唯一约束：分类名称全局唯一
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conrelid = 'category'::regclass 
        AND conname = 'uk_category_name'
    ) THEN
        ALTER TABLE category 
        ADD CONSTRAINT uk_category_name 
        UNIQUE (name);
    END IF;
END $$;

-- ============================================
-- 第三部分：创建索引
-- ============================================

-- role表索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_role_code ON role(role_code);

-- user表索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_name ON "usr"(user_name);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON "usr"(email);
CREATE INDEX IF NOT EXISTS idx_user_phone ON "usr"(phone_number);
CREATE INDEX IF NOT EXISTS idx_user_role ON "usr"(role_id);

-- address表索引
CREATE INDEX IF NOT EXISTS idx_address_user ON address(user_id);

-- merchant表索引
CREATE INDEX IF NOT EXISTS idx_merchant_user ON merchant(user_id);
CREATE INDEX IF NOT EXISTS idx_merchant_status ON merchant(status);
CREATE INDEX IF NOT EXISTS idx_merchant_user_status ON merchant(user_id, status);

-- shop表索引
CREATE INDEX IF NOT EXISTS idx_shop_merchant ON shop(merchant_id);
CREATE INDEX IF NOT EXISTS idx_shop_status ON shop(status);
CREATE INDEX IF NOT EXISTS idx_shop_merchant_status ON shop(merchant_id, status);

-- product表索引
CREATE INDEX IF NOT EXISTS idx_product_shop ON product(shop_id);
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_product_shop_status ON product(shop_id, status);
-- 商品名称全文搜索索引（使用simple分词器）
CREATE INDEX IF NOT EXISTS idx_product_name_gin ON product USING gin(to_tsvector('simple', name));

-- sku表索引
CREATE INDEX IF NOT EXISTS idx_sku_product ON sku(product_id);
CREATE INDEX IF NOT EXISTS idx_sku_price ON sku(price);
CREATE INDEX IF NOT EXISTS idx_sku_stock ON sku(stock);
-- 部分索引：只索引有库存的SKU
CREATE INDEX IF NOT EXISTS idx_sku_product_stock ON sku(product_id, stock) WHERE stock > 0;

-- category表索引
CREATE INDEX IF NOT EXISTS idx_category_parent ON category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_name ON category(name);

-- product_category表索引（复合主键已自动创建索引）
CREATE INDEX IF NOT EXISTS idx_pc_category ON product_category(category_id);

-- cart_item表索引（复合主键已自动创建索引）
CREATE INDEX IF NOT EXISTS idx_cart_user ON cart_item(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_sku ON cart_item(sku_id);

-- batch_order表索引
CREATE INDEX IF NOT EXISTS idx_batch_order_user ON batch_order(user_id);
CREATE INDEX IF NOT EXISTS idx_batch_order_status ON batch_order(status);
CREATE INDEX IF NOT EXISTS idx_batch_order_create_time ON batch_order(create_time DESC);
CREATE INDEX IF NOT EXISTS idx_batch_order_expire_time ON batch_order(expire_time);
CREATE INDEX IF NOT EXISTS idx_batch_order_user_status_time ON batch_order(user_id, status, create_time DESC);

-- order表索引
CREATE INDEX IF NOT EXISTS idx_order_batch_order ON "order"(batch_order_id);
CREATE INDEX IF NOT EXISTS idx_order_user ON "order"(user_id);
CREATE INDEX IF NOT EXISTS idx_order_shop ON "order"(shop_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON "order"(status);
CREATE INDEX IF NOT EXISTS idx_order_create_time ON "order"(create_time DESC);
CREATE INDEX IF NOT EXISTS idx_order_user_status_time ON "order"(user_id, status, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_order_shop_status_time ON "order"(shop_id, status, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_order_batch_status ON "order"(batch_order_id, status);

-- order_item表索引（复合主键已自动创建索引）
CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_sku ON order_item(sku_id);

-- audit_log表索引
CREATE INDEX IF NOT EXISTS idx_audit_target ON audit_log(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_audit_admin ON audit_log(admin_user_id);
CREATE INDEX IF NOT EXISTS idx_audit_time ON audit_log(create_time DESC);
CREATE INDEX IF NOT EXISTS idx_audit_target_time ON audit_log(target_type, target_id, create_time DESC);

-- ============================================
-- 第四部分：初始化角色
-- ============================================

-- 管理员角色
INSERT INTO role (role_code, role_name, description, create_time, update_time)
VALUES ('ADMIN', '管理员', '系统管理员角色', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_code) DO NOTHING;

-- 普通用户角色
INSERT INTO role (role_code, role_name, description, create_time, update_time)
VALUES ('USER', '普通用户', '普通用户角色', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_code) DO NOTHING;

-- 商家角色
INSERT INTO role (role_code, role_name, description, create_time, update_time)
VALUES ('MERCHANT', '商家', '商家角色', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (role_code) DO NOTHING;

-- ============================================
-- 第五部分：初始化基础分类
-- ============================================
-- 创建三级分类结构：
-- 一级分类（6个）
--   每个一级分类包含2个二级分类
--   每个二级分类包含2个三级分类
-- 总共：6个一级分类 + 12个二级分类 + 24个三级分类

-- 一级分类
WITH level1_categories AS (
    INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
    VALUES 
        (NULL, '电子产品', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (NULL, '服装配饰', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (NULL, '家居用品', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (NULL, '美妆护肤', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (NULL, '图书文教', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (NULL, '玩具游戏', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO UPDATE SET sort_order = EXCLUDED.sort_order
    RETURNING category_id, name
)
-- 二级分类：电子产品
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    c1.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_categories c1
CROSS JOIN (VALUES 
    ('手机通讯', 2),
    ('电脑数码', 2)
) AS sub(name, sort_order)
WHERE c1.name = '电子产品'
ON CONFLICT (name) DO NOTHING;

-- 二级分类：服装配饰
WITH level1_cat AS (
    SELECT category_id FROM category WHERE name = '服装配饰' AND parent_id IS NULL
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level1_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_cat
CROSS JOIN (VALUES 
    ('男装', 2),
    ('女装', 2)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 二级分类：家居用品
WITH level1_cat AS (
    SELECT category_id FROM category WHERE name = '家居用品' AND parent_id IS NULL
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level1_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_cat
CROSS JOIN (VALUES 
    ('家具', 2),
    ('家纺', 2)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 二级分类：美妆护肤
WITH level1_cat AS (
    SELECT category_id FROM category WHERE name = '美妆护肤' AND parent_id IS NULL
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level1_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_cat
CROSS JOIN (VALUES 
    ('面部护肤', 2),
    ('彩妆', 2)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 二级分类：图书文教
WITH level1_cat AS (
    SELECT category_id FROM category WHERE name = '图书文教' AND parent_id IS NULL
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level1_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_cat
CROSS JOIN (VALUES 
    ('图书', 2),
    ('文具', 2)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 二级分类：玩具游戏
WITH level1_cat AS (
    SELECT category_id FROM category WHERE name = '玩具游戏' AND parent_id IS NULL
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level1_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level1_cat
CROSS JOIN (VALUES 
    ('儿童玩具', 2),
    ('电子游戏', 2)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：手机通讯的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '电子产品' AND c1.parent_id IS NULL
    AND c2.name = '手机通讯'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('智能手机', 3),
    ('手机配件', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：电脑数码的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '电子产品' AND c1.parent_id IS NULL
    AND c2.name = '电脑数码'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('笔记本电脑', 3),
    ('数码配件', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：男装的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '服装配饰' AND c1.parent_id IS NULL
    AND c2.name = '男装'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('T恤衬衫', 3),
    ('外套夹克', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：女装的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '服装配饰' AND c1.parent_id IS NULL
    AND c2.name = '女装'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('连衣裙', 3),
    ('上衣', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：家具的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '家居用品' AND c1.parent_id IS NULL
    AND c2.name = '家具'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('沙发', 3),
    ('桌椅', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：家纺的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '家居用品' AND c1.parent_id IS NULL
    AND c2.name = '家纺'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('床品', 3),
    ('窗帘', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：面部护肤的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '美妆护肤' AND c1.parent_id IS NULL
    AND c2.name = '面部护肤'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('洁面', 3),
    ('面霜', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：彩妆的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '美妆护肤' AND c1.parent_id IS NULL
    AND c2.name = '彩妆'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('口红', 3),
    ('粉底', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：图书的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '图书文教' AND c1.parent_id IS NULL
    AND c2.name = '图书'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('小说', 3),
    ('教育', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：文具的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '图书文教' AND c1.parent_id IS NULL
    AND c2.name = '文具'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('笔类', 3),
    ('本子', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：儿童玩具的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '玩具游戏' AND c1.parent_id IS NULL
    AND c2.name = '儿童玩具'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('积木', 3),
    ('模型', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- 三级分类：电子游戏的子分类
WITH level2_cat AS (
    SELECT c2.category_id FROM category c1
    JOIN category c2 ON c2.parent_id = c1.category_id
    WHERE c1.name = '玩具游戏' AND c1.parent_id IS NULL
    AND c2.name = '电子游戏'
)
INSERT INTO category (parent_id, name, sort_order, create_time, update_time)
SELECT 
    level2_cat.category_id,
    sub.name,
    sub.sort_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM level2_cat
CROSS JOIN (VALUES 
    ('游戏主机', 3),
    ('游戏软件', 3)
) AS sub(name, sort_order)
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- 完成
-- ============================================
-- 脚本执行完成
-- 
-- 验证查询：
-- SELECT COUNT(*) as level1_count FROM category WHERE parent_id IS NULL;
-- SELECT COUNT(*) as level2_count FROM category c1 
--   JOIN category c2 ON c2.parent_id = c1.category_id 
--   WHERE c1.parent_id IS NULL;
-- SELECT COUNT(*) as level3_count FROM category c1
--   JOIN category c2 ON c2.parent_id = c1.category_id
--   JOIN category c3 ON c3.parent_id = c2.category_id
--   WHERE c1.parent_id IS NULL;

