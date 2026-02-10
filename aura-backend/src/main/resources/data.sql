-- ============================================
-- Aura Quiet Living - Database Initialization (Integrated Version)
-- ============================================
-- 更新日期: 2026-02-05
-- 修改点: 整合本地图片路径 (/images/products/...) 并修复转义错误
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS aura_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aura_db;

-- 删除已存在的表（开发环境）
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS chat_history;
DROP TABLE IF EXISTS products;

-- ============================================
-- 1. 产品表 (products)
-- ============================================
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY COMMENT '产品ID',
    name VARCHAR(200) NOT NULL COMMENT '产品名称',
    category VARCHAR(50) NOT NULL COMMENT '分类: Audio/Wearable/Mobile/Home',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    description TEXT COMMENT '产品描述',
    features JSON COMMENT '特性列表（JSON数组）',
    image_url VARCHAR(500) COMMENT '产品图片URL',
    manual_path VARCHAR(500) COMMENT 'PDF说明书路径',
    stock INT DEFAULT 100 COMMENT '库存数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- ============================================
-- 2. 订单表 (orders)
-- ============================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
    user_id VARCHAR(50) DEFAULT 'demo_user' COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    shipping_address TEXT NOT NULL COMMENT '配送地址',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态',
    payment_method VARCHAR(50) DEFAULT 'MOCK' COMMENT '支付方式',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_number (order_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================
-- 3. 订单项表 (order_items)
-- ============================================
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id VARCHAR(50) NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================
-- 4. 聊天历史表 (chat_history)
-- ============================================
CREATE TABLE chat_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色（user/assistant）',
    message TEXT NOT NULL COMMENT '消息内容',
    context_data JSON COMMENT '上下文数据（JSON格式）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天历史表';

-- ============================================
-- 插入初始数据 - 6 个产品 (图片路径已本地化)
-- ============================================

-- 产品 1: Aura Harmony (耳机)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p1', 'Aura Harmony', 'Audio', 429.00, 
'Experience the pinnacle of audio with Aura Harmony. These premium wireless headphones deliver immersive, high-resolution sound.',
'["Bluetooth 5.0 Connectivity", "30h Battery Life (ANC On)", "Advanced Active Noise Cancellation (ANC)"]',
'/images/products/aura-harmony.jpg', 'manuals/aura-harmony.pdf', 50);

-- 产品 2: Aura Pulse (智能手表)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p2', 'Aura Pulse', 'Wearable', 329.00,
'Stay connected to your body and the world with Aura Pulse. This elegant smartwatch combines health tracking and timeless design.',
'["Heart Rate & SpO2 Monitoring", "Sleep Tracking & Analysis", "7-Day Battery Life"]',
'/images/products/aura-pulse.jpg', 'manuals/aura-pulse.pdf', 80);

-- 产品 3: Aura Flow (智能手机) - 修复 JSON 引号转义
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p3', 'Aura Flow', 'Mobile', 899.00,
'A smartphone that flows with your life. Aura Flow features a stunning display and powerful performance.',
'["6.5\\" OLED Display (120Hz)", "Triple Camera System (50MP Main)", "8GB RAM + 256GB Storage"]',
'/images/products/aura-flow.jpg', 'manuals/aura-flow.pdf', 30);

-- 产品 4: Aura Breeze (空气净化器)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p4', 'Aura Breeze', 'Home', 499.00,
'Breathe pure, breathe calm. Aura Breeze is a whisper-quiet air purifier.',
'["HEPA H13 Filter (99.97% Filtration)", "Covers up to 800 sq ft", "Ultra-Quiet Operation (<25dB)"]',
'/images/products/aura-breeze.jpg', 'manuals/aura-breeze.pdf', 60);

-- 产品 5: Aura Echo (智能音箱)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p5', 'Aura Echo', 'Home', 199.00,
'Your voice, amplified. Aura Echo is a smart speaker that fills your room with rich, 360-degree sound.',
'["360° Sound with Deep Bass", "Voice Assistant Built-in", "Smart Home Hub (Zigbee & Wi-Fi)"]',
'/images/products/aura-echo.jpg', 'manuals/aura-echo.pdf', 100);

-- 产品 6: Aura Slate (平板电脑) - 修复单引号及 JSON 引号转义
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p6', 'Aura Slate', 'Mobile', 699.00,
'Creativity meets productivity. Aura Slate is a premium tablet designed for creators. It\'s your canvas for ideas.',
'["11\\" Liquid Retina Display (2K)", "Octa-Core Processor", "6GB RAM + 128GB Storage"]',
'/images/products/aura-slate.jpg', 'manuals/aura-slate.pdf', 40);

-- ============================================
-- 插入示例订单数据
-- ============================================

INSERT INTO orders (order_number, user_id, total_amount, shipping_address, status, payment_method) VALUES
('ORD-20260127-001', 'demo_user', 758.00, '123 Technology Drive, San Francisco, CA', 'DELIVERED', 'MOCK'),
('ORD-20260127-002', 'demo_user', 899.00, '456 Innovation Blvd, Austin, TX', 'SHIPPED', 'MOCK');

INSERT INTO order_items (order_id, product_id, product_name, quantity, price, subtotal) VALUES
(1, 'p1', 'Aura Harmony', 1, 429.00, 429.00),
(1, 'p2', 'Aura Pulse', 1, 329.00, 329.00),
(2, 'p3', 'Aura Flow', 1, 899.00, 899.00);

-- ============================================
-- 插入示例聊天历史 - 修复单引号转义
-- ============================================

INSERT INTO chat_history (session_id, user_id, role, content) VALUES
('session_001', 'demo_user', 'USER', 'Hi, I\'m looking for wireless headphones.'),
('session_001', 'demo_user', 'ASSISTANT', 'Hello! Based on your needs, I recommend the **Aura Harmony**.'),
('session_001', 'demo_user', 'USER', 'What\'s the Bluetooth range?'),
('session_001', 'demo_user', 'ASSISTANT', 'The Aura Harmony uses Bluetooth 5.0 with a range of 30 feet.');

-- ============================================
-- 验证更新
-- ============================================
SELECT id, name, image_url FROM products ORDER BY id;