-- ============================================
-- Aura Quiet Living - Database Initialization
-- ============================================
-- 创建日期: 2026-01-27
-- 说明: 包含所有表结构和初始数据
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS aura_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aura_db;

-- 删除已存在的表（开发环境）
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS chat_history;
DROP TABLE IF EXISTS product_manuals;
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
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态: PENDING/PAID/SHIPPED/DELIVERED',
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
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称（冗余存储）',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    price DECIMAL(10, 2) NOT NULL COMMENT '单价（下单时价格）',
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
    user_id VARCHAR(50) DEFAULT 'demo_user' COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: USER/ASSISTANT/SYSTEM',
    content TEXT NOT NULL COMMENT '消息内容',
    metadata JSON COMMENT '元数据（如函数调用记录）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天历史表';

-- ============================================
-- 5. 产品说明书表 (product_manuals)
-- ============================================
CREATE TABLE product_manuals (
    id VARCHAR(50) PRIMARY KEY COMMENT '文档ID',
    product_id VARCHAR(50) NOT NULL COMMENT '关联产品ID',
    content TEXT NOT NULL COMMENT '文档内容（分块后的文本）',
    chunk_index INT NOT NULL COMMENT '分块索引',
    metadata JSON COMMENT '元数据（如页码、章节）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_chunk_index (chunk_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品说明书表';

-- ============================================
-- 插入初始数据 - 6 个产品
-- ============================================

-- 产品 1: Aura Harmony (耳机)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p1', 'Aura Harmony', 'Audio', 429.00, 
'Experience the pinnacle of audio with Aura Harmony. These premium wireless headphones deliver immersive, high-resolution sound with adaptive noise cancellation and luxurious comfort. Crafted for the discerning listener, they transform every moment into a private concert.',
'["Bluetooth 5.0 Connectivity", "30h Battery Life (ANC On)", "Advanced Active Noise Cancellation (ANC)", "Premium Memory Foam Earcups", "Voice Assistant Support", "Foldable Design with Carrying Case"]',
'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800',
'manuals/aura-harmony.pdf',
50);

-- 产品 2: Aura Pulse (智能手表)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p2', 'Aura Pulse', 'Wearable', 329.00,
'Stay connected to your body and the world with Aura Pulse. This elegant smartwatch combines health tracking, notifications, and timeless design. Monitor your heart, track your sleep, and move through life with mindful awareness.',
'["Heart Rate & SpO2 Monitoring", "Sleep Tracking & Analysis", "7-Day Battery Life", "Water Resistant (5ATM)", "Customizable Watch Faces", "Smartphone Notifications"]',
'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800',
'manuals/aura-pulse.pdf',
80);

-- 产品 3: Aura Flow (智能手机)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p3', 'Aura Flow', 'Mobile', 899.00,
'A smartphone that flows with your life. Aura Flow features a stunning display, powerful performance, and an intuitive interface designed for seamless interaction. Capture moments, stay productive, and enjoy entertainment—all with effortless grace.',
'["6.5\" OLED Display (120Hz)", "Triple Camera System (50MP Main)", "8GB RAM + 256GB Storage", "5000mAh Battery with Fast Charging", "5G Connectivity", "Face & Fingerprint Unlock"]',
'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800',
'manuals/aura-flow.pdf',
30);

-- 产品 4: Aura Breeze (空气净化器)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p4', 'Aura Breeze', 'Home', 499.00,
'Breathe pure, breathe calm. Aura Breeze is a whisper-quiet air purifier that removes 99.97% of airborne particles, creating a sanctuary of clean air in your home. With smart sensors and elegant design, it blends seamlessly into your space.',
'["HEPA H13 Filter (99.97% Filtration)", "Covers up to 800 sq ft", "Ultra-Quiet Operation (<25dB)", "Air Quality Sensor & Display", "Auto Mode & Sleep Mode", "App Control & Scheduling"]',
'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800',
'manuals/aura-breeze.pdf',
60);

-- 产品 5: Aura Echo (智能音箱)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p5', 'Aura Echo', 'Home', 199.00,
'Your voice, amplified. Aura Echo is a smart speaker that fills your room with rich, 360-degree sound. Ask questions, control your smart home, and enjoy your favorite music—all with the power of your voice.',
'["360° Sound with Deep Bass", "Voice Assistant Built-in", "Smart Home Hub (Zigbee & Wi-Fi)", "Multi-Room Audio Support", "Touch Controls & LED Display", "Spotify, Apple Music, Amazon Music Support"]',
'https://images.unsplash.com/photo-1589492477829-5e65395b66cc?w=800',
'manuals/aura-echo.pdf',
100);

-- 产品 6: Aura Slate (平板电脑)
INSERT INTO products (id, name, category, price, description, features, image_url, manual_path, stock) VALUES
('p6', 'Aura Slate', 'Mobile', 699.00,
'Creativity meets productivity. Aura Slate is a premium tablet designed for creators, students, and professionals. With a stunning display, powerful processor, and optional stylus support, it's your canvas for ideas.',
'["11\" Liquid Retina Display (2K)", "Octa-Core Processor", "6GB RAM + 128GB Storage", "Stylus Support (sold separately)", "Quad Speakers with Dolby Atmos", "14-Hour Battery Life"]',
'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800',
'manuals/aura-slate.pdf',
40);

-- ============================================
-- 插入示例订单数据（用于测试）
-- ============================================

-- 示例订单 1
INSERT INTO orders (order_number, user_id, total_amount, shipping_address, status, payment_method) VALUES
('ORD-20260127-001', 'demo_user', 758.00, '123 Technology Drive, Suite 400, San Francisco, CA 94105, United States', 'DELIVERED', 'MOCK');

INSERT INTO order_items (order_id, product_id, product_name, quantity, price, subtotal) VALUES
(1, 'p1', 'Aura Harmony', 1, 429.00, 429.00),
(1, 'p2', 'Aura Pulse', 1, 329.00, 329.00);

-- 示例订单 2
INSERT INTO orders (order_number, user_id, total_amount, shipping_address, status, payment_method) VALUES
('ORD-20260127-002', 'demo_user', 899.00, '456 Innovation Blvd, Austin, TX 78701, United States', 'SHIPPED', 'MOCK');

INSERT INTO order_items (order_id, product_id, product_name, quantity, price, subtotal) VALUES
(2, 'p3', 'Aura Flow', 1, 899.00, 899.00);

-- ============================================
-- 插入示例聊天历史（用于测试）
-- ============================================

INSERT INTO chat_history (session_id, user_id, role, content) VALUES
('session_001', 'demo_user', 'USER', 'Hi, I\'m looking for wireless headphones with good noise cancellation.'),
('session_001', 'demo_user', 'ASSISTANT', 'Hello! I\'d be happy to help you find the perfect headphones. Based on your needs, I recommend the **Aura Harmony**. It features advanced active noise cancellation (ANC), Bluetooth 5.0 connectivity, and an impressive 30-hour battery life. The premium memory foam earcups ensure luxurious comfort for extended listening sessions. Would you like to know more about its features?'),
('session_001', 'demo_user', 'USER', 'What\'s the Bluetooth range?'),
('session_001', 'demo_user', 'ASSISTANT', 'The Aura Harmony uses Bluetooth 5.0, which typically provides a stable connection up to 30 feet (10 meters) in open space. This means you can move around your room or office without losing connection to your device. Is there anything else you\'d like to know?');

-- ============================================
-- 验证数据插入
-- ============================================

-- 查看插入的产品数量
SELECT COUNT(*) AS product_count FROM products;

-- 查看插入的订单数量
SELECT COUNT(*) AS order_count FROM orders;

-- 查看插入的聊天记录数量
SELECT COUNT(*) AS chat_count FROM chat_history;

-- ============================================
-- 完成
-- ============================================
-- 数据库初始化完成！
-- 包含：
-- - 5 个表（products, orders, order_items, chat_history, product_manuals）
-- - 6 个产品数据
-- - 2 个示例订单
-- - 4 条示例聊天记录
-- ============================================
