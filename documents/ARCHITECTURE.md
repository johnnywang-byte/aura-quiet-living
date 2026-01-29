# Aura 电商项目 - 完整架构手册

> **版本**: v1.0  
> **更新时间**: 2026-01-27  
> **项目类型**: Spring AI Agent 演示项目  
> **团队规模**: 6 人  
> **开发周期**: 8-9 天

---

## 📑 目录

1. [项目概述](#1-项目概述)
2. [技术架构](#2-技术架构)
3. [AI Agent 能力](#3-ai-agent-能力)
4. [数据库设计](#4-数据库设计)
5. [API 设计](#5-api-设计)
6. [项目结构](#6-项目结构)
7. [配置指南](#7-配置指南)
8. [开发计划](#8-开发计划)
9. [演示脚本](#9-演示脚本)
10. [部署指南](#10-部署指南)

---

## 1. 项目概述

### 1.1 项目定位

**Aura** 是一个展示 Spring AI Agent 完整能力的电商演示项目，重点在于 AI 技术展示而非电商功能的完整性。

### 1.2 核心目标

✅ **展示 10 个 Spring AI Agent 核心能力**  
✅ **简化电商功能，聚焦 AI 演示**  
✅ **8-9 天内完成开发和演示准备**  
✅ **6 人团队高效协作**

### 1.3 项目特点

| 方面 | 策略 |
|------|------|
| **AI 功能** | 完整实现（10 个核心能力） |
| **电商功能** | 简化实现（基础展示） |
| **用户系统** | 单用户模式（无认证） |
| **支付流程** | 模拟支付（自动成功） |
| **部署复杂度** | 最小化（SimpleVectorStore） |

---

## 2. 技术架构

### 2.1 技术栈

#### 后端
```
Spring Boot 3.2+
├── Spring AI (OpenAI Integration)
├── MySQL 8.0 (数据持久化)
├── SimpleVectorStore (向量存储)
├── Apache PDFBox (PDF 解析)
└── Spring Data JPA (ORM)
```

#### 前端
```
React 19.2.0
├── TypeScript
├── Vite (构建工具)
└── Tailwind CSS (样式)
```

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────┐
│                    前端 (React)                      │
│  - 产品展示页                                        │
│  - AI 聊天界面                                       │
│  - 订单管理页                                        │
└─────────────┬───────────────────────────────────────┘
              │ HTTP/REST API
              ↓
┌─────────────────────────────────────────────────────┐
│              后端 (Spring Boot)                      │
│                                                      │
│  ┌──────────────────────────────────────────┐      │
│  │     Orchestrator Agent (编排者)          │      │
│  │  - 任务分解                               │      │
│  │  - Agent 调度                             │      │
│  │  - 结果整合                               │      │
│  └────────┬─────────────────────────────────┘      │
│           │                                          │
│    ┌──────┴──────┐                                  │
│    ↓             ↓                                  │
│  ┌─────────┐  ┌──────────┐                         │
│  │ Product │  │ Customer │                         │
│  │ Expert  │  │ Service  │                         │
│  │ Agent   │  │ Agent    │                         │
│  └─────────┘  └──────────┘                         │
│                                                      │
│  ┌──────────────────────────────────────────┐      │
│  │         AI 核心服务                       │      │
│  │  - RAG Service (检索增强)                │      │
│  │  - Memory Service (对话记忆)             │      │
│  │  - Function Service (工具调用)           │      │
│  └──────────────────────────────────────────┘      │
└─────────┬───────────────┬───────────────────────────┘
          │               │
          ↓               ↓
    ┌─────────┐    ┌──────────────┐
    │  MySQL  │    │SimpleVector  │
    │         │    │   Store      │
    └─────────┘    └──────────────┘
          │
          ↓
    ┌─────────────┐
    │  OpenAI API │
    │ (gpt-4o-mini)│
    └─────────────┘
```

### 2.3 去掉的功能（简化）

- ❌ 用户注册/登录（JWT 认证）
- ❌ 真实支付集成
- ❌ Redis 缓存（使用内存缓存）
- ❌ 复杂的权限管理
- ❌ 邮件通知
- ❌ 第三方物流集成

---

## 3. AI Agent 能力

### 3.1 十大核心能力

| # | 能力 | 说明 | 演示场景 |
|---|------|------|---------|
| 1 | **Prompt Engineering** | 品牌个性化对话 | "你好" → 温暖的品牌介绍 |
| 2 | **RAG** | 检索增强生成 | "蓝牙传输距离是多少？" → 从 PDF 检索 |
| 3 | **Memory** | 多层记忆系统 | "我收到的这个颜色不对" → 理解上下文 |
| 4 | **Function Calling** | 工具调用 | "帮我看看订单状态" → 自动调用接口 |
| 5 | **Complex Task** | 复杂任务分解 | "把订单送到新地址 XXX" → 多步骤执行 |
| 6 | **Multi-Agent** | 多 Agent 协作 | "推荐耳机 + 查订单" → 两个 Agent 协作 |
| 7 | **Planning** | 任务规划 | "给办公室配设备，预算 3000" → 智能规划 |
| 8 | **Tool Chain** | 工具链编排 | "对比所有耳机库存" → 工具自动组合 |
| 9 | **Error Handling** | 智能错误处理 | 订单号不存在 → 友好提示 |
| 10 | **Reflection** | 自我反思优化 | 回复质量评估 → 自我改进 |

### 3.2 Agent 层次结构

```
OrchestratorAgent (主编排者)
│
├── ProductExpertAgent (产品专家)
│   ├── searchProducts()
│   ├── getProductDetail()
│   ├── compareProducts()
│   └── queryProductManual()
│
└── CustomerServiceAgent (客服专员)
    ├── getOrderStatus()
    ├── updateOrderAddress()
    ├── createReturnRequest()
    └── checkInventory()
```

### 3.3 核心 AI 服务

#### RAGService (检索增强生成)
```java
@Service
public class RAGService {
    @Autowired private SimpleVectorStore vectorStore;
    @Autowired private OpenAiChatClient chatClient;
    
    public String answerFromManual(String question, String productId) {
        // 1. 向量检索
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(3)
        );
        
        // 2. 构建上下文
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));
        
        // 3. 生成回答
        return chatClient.call(buildPrompt(context, question));
    }
}
```

#### MemoryService (三层记忆)
```java
@Service
public class MemoryService {
    // 短期记忆（内存）
    private Map<String, ConversationBuffer> shortTermMemory;
    
    // 长期记忆（MySQL）
    @Autowired private ChatHistoryRepository chatHistoryRepository;
    
    // 语义记忆（向量存储）
    @Autowired private SimpleVectorStore semanticMemory;
    
    public void saveMessage(String sessionId, ChatMessage message) {
        // 三层存储
        shortTermMemory.get(sessionId).add(message);
        chatHistoryRepository.save(toEntity(message));
        semanticMemory.add(List.of(toDocument(message)));
    }
}
```

#### Function Calling (8 个核心函数)
```java
// 1. 查询订单状态
@Component
@Description("查询订单状态和物流信息")
public class GetOrderStatusFunction implements Function<Request, Response> {
    // 实现...
}

// 2. 修改订单地址
@Component
@Description("修改订单配送地址")
public class UpdateOrderAddressFunction { /* ... */ }

// 3-8. 其他函数...
```

---

## 4. 数据库设计

### 4.1 核心表结构（MySQL）

#### products（产品表）
```sql
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    tagline VARCHAR(500),
    description TEXT,
    long_description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    image_url VARCHAR(500),
    stock INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### orders（订单表）
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    customer_email VARCHAR(100),
    customer_phone VARCHAR(20),
    shipping_address TEXT,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    payment_method VARCHAR(50) DEFAULT 'MOCK',
    payment_status VARCHAR(50) DEFAULT 'PAID',
    tracking_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_number (order_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### order_items（订单商品）
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    product_name VARCHAR(200),
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### chat_history（对话历史）⭐
```sql
CREATE TABLE chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,  -- 'user' or 'assistant'
    message TEXT NOT NULL,
    context_data JSON,           -- 存储上下文（订单号、产品 ID 等）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### product_manuals（产品说明书）⭐
```sql
CREATE TABLE product_manuals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    chunk_index INT NOT NULL,
    page_number INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 4.2 初始化数据

**data.sql:**
```sql
-- 产品数据
INSERT INTO products (id, name, tagline, description, price, category, image_url, stock) VALUES
('p1', 'Aura Harmony', 'Listen naturally.', 'Audio that feels like the open air...', 429.00, 'Audio', 'https://...', 50),
('p2', 'Aura Epoch', 'Moments, not minutes.', 'A timepiece designed for wellness...', 349.00, 'Wearable', 'https://...', 30),
('p3', 'Aura Canvas', 'Capture the warmth.', 'A display that mimics paper...', 1099.00, 'Mobile', 'https://...', 20),
('p4', 'Aura Essence', 'Return to nature.', 'An air purifier that doubles as sculpture...', 599.00, 'Home', 'https://...', 25),
('p5', 'Aura Beam', 'Light that breathes.', 'Smart circadian lighting...', 249.00, 'Home', 'https://...', 40),
('p6', 'Aura Scribe', 'Thought in motion.', 'A digital stylus with graphite friction...', 129.00, 'Mobile', 'https://...', 60);

-- 演示订单
INSERT INTO orders (order_number, customer_name, customer_email, shipping_address, total_amount, status, payment_status) VALUES
('ORD-20260127-001', '张三', 'zhangsan@example.com', '北京市朝阳区建国路 88 号', 429.00, 'SHIPPED', 'PAID');

INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES
(1, 'p1', 'Aura Harmony', 1, 429.00);
```

---

## 5. API 设计

### 5.1 产品 API

```
GET    /api/products                    # 获取所有产品
GET    /api/products/{id}               # 获取产品详情
GET    /api/products/category/{category} # 按分类获取
GET    /api/products/search?q={keyword} # 搜索产品
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "id": "p1",
    "name": "Aura Harmony",
    "price": 429.00,
    "category": "Audio",
    "stock": 50,
    "imageUrl": "https://..."
  }
}
```

### 5.2 订单 API

```
POST   /api/orders                      # 创建订单（模拟支付）
GET    /api/orders/{orderNumber}        # 获取订单详情
GET    /api/orders                      # 获取订单列表
PUT    /api/orders/{orderNumber}/address # 修改订单地址
```

**创建订单请求:**
```json
{
  "customerName": "张三",
  "customerEmail": "zhangsan@example.com",
  "shippingAddress": "北京市朝阳区...",
  "items": [
    {
      "productId": "p1",
      "quantity": 1
    }
  ]
}
```

**创建订单响应:**
```json
{
  "success": true,
  "data": {
    "orderNumber": "ORD-20260127-002",
    "totalAmount": 429.00,
    "status": "PENDING",
    "paymentStatus": "PAID",  // 自动成功
    "message": "订单创建成功，支付已完成"
  }
}
```

### 5.3 AI 助手 API ⭐

```
POST   /api/ai/chat                     # 发送消息
GET    /api/ai/chat/history/{sessionId} # 获取聊天历史
DELETE /api/ai/chat/history/{sessionId} # 清除聊天历史
```

**聊天请求:**
```json
{
  "message": "我想买个耳机",
  "sessionId": "uuid-session-id",
  "context": {
    "currentPage": "products",
    "viewingProductId": null
  }
}
```

**聊天响应:**
```json
{
  "success": true,
  "data": {
    "message": "为您推荐 Aura Harmony 耳机，采用开放式声学设计...",
    "sessionId": "uuid-session-id",
    "suggestedProducts": ["p1"],
    "suggestedActions": [
      {
        "type": "VIEW_PRODUCT",
        "productId": "p1",
        "label": "查看详情"
      }
    ],
    "timestamp": "2026-01-27T16:00:00Z"
  }
}
```

---

## 6. 项目结构

### 6.1 后端结构

```
aura-backend/
├── src/main/java/com/aura/
│   ├── AuraApplication.java
│   │
│   ├── config/
│   │   ├── OpenAIConfig.java           # OpenAI 配置
│   │   ├── VectorStoreConfig.java      # SimpleVectorStore 配置
│   │   └── CorsConfig.java             # CORS 配置
│   │
│   ├── controller/
│   │   ├── ProductController.java      # 产品 API
│   │   ├── OrderController.java        # 订单 API
│   │   └── AIController.java           # AI 助手 API
│   │
│   ├── service/
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   │
│   │   └── ai/
│   │       ├── AIAgentService.java     # 核心 AI 服务
│   │       ├── RAGService.java         # RAG 实现
│   │       ├── MemoryService.java      # 对话记忆
│   │       ├── MultiAgentService.java  # 多 Agent 编排
│   │       └── PDFVectorizationService.java # PDF 向量化
│   │
│   ├── repository/
│   │   ├── ProductRepository.java
│   │   ├── OrderRepository.java
│   │   ├── ChatHistoryRepository.java
│   │   └── ProductManualRepository.java
│   │
│   ├── model/
│   │   ├── entity/
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── ChatHistory.java
│   │   │   └── ProductManual.java
│   │   │
│   │   └── dto/
│   │       ├── ChatRequest.java
│   │       ├── ChatResponse.java
│   │       └── OrderRequest.java
│   │
│   ├── ai/
│   │   ├── agent/
│   │   │   ├── OrchestratorAgent.java
│   │   │   ├── ProductExpertAgent.java
│   │   │   └── CustomerServiceAgent.java
│   │   │
│   │   ├── function/
│   │   │   ├── GetOrderStatusFunction.java
│   │   │   ├── UpdateOrderAddressFunction.java
│   │   │   ├── SearchProductsFunction.java
│   │   │   ├── GetProductDetailFunction.java
│   │   │   ├── CompareProductsFunction.java
│   │   │   ├── CheckInventoryFunction.java
│   │   │   ├── CreateReturnRequestFunction.java
│   │   │   └── QueryProductManualFunction.java
│   │   │
│   │   └── prompt/
│   │       └── SystemPrompts.java
│   │
│   └── util/
│       └── PDFParser.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── data.sql
│   └── manuals/                        # 产品说明书 PDF
│       ├── aura-harmony-manual.pdf
│       ├── aura-epoch-manual.pdf
│       └── ...
│
└── pom.xml
```

### 6.2 前端结构

```
aura-frontend/
├── src/
│   ├── components/
│   │   ├── Navbar.tsx
│   │   ├── Hero.tsx
│   │   ├── ProductGrid.tsx
│   │   ├── ProductDetail.tsx
│   │   ├── AIAssistant.tsx             # AI 聊天界面
│   │   ├── CartDrawer.tsx
│   │   ├── Checkout.tsx                # 模拟支付
│   │   └── Footer.tsx
│   │
│   ├── services/
│   │   └── api.ts                      # API 调用服务
│   │
│   ├── types.ts
│   ├── App.tsx
│   └── index.tsx
│
├── index.html
├── package.json
└── vite.config.ts
```

---

## 7. 配置指南

### 7.1 后端配置

**application.yml:**
```yaml
spring:
  application:
    name: aura-backend
  
  # MySQL 配置
  datasource:
    url: jdbc:mysql://localhost:3306/aura_db?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  # OpenAI 配置
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7
          max-tokens: 1000
      embedding:
        options:
          model: text-embedding-3-small
    
    # SimpleVectorStore 配置
    vectorstore:
      simple:
        file-path: ./data/vector-store.json
        auto-save-interval: 60000

server:
  port: 8080

# CORS 配置
cors:
  allowed-origins: http://localhost:5173

# 日志配置
logging:
  level:
    com.aura: DEBUG
    org.springframework.ai: DEBUG
```

**环境变量（.env）:**
```bash
OPENAI_API_KEY=sk-your-api-key-here
DB_PASSWORD=your-mysql-password
```

### 7.2 前端配置

**.env.local:**
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

**vite.config.ts:**
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
```

### 7.3 Maven 依赖

**pom.xml:**
```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring AI OpenAI -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
        <version>1.0.0-M5</version>
    </dependency>
    
    <!-- MySQL -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    
    <!-- PDF 处理 -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.1</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 8. 开发计划

### 8.1 团队分工（6 人）

| 成员 | 职责 | 主要任务 | 工作量 |
|------|------|---------|--------|
| **P1** | AI 核心 | RAG、Memory、向量化 | 4 天 |
| **P2** | Function + Agent | 8 个 Function、多 Agent 编排 | 4 天 |
| **P3** | 数据库 + 数据 | MySQL 设计、产品数据、PDF 说明书 | 3 天 |
| **P4** | 前端页面 | 产品页、订单页、API 集成 | 3 天 |
| **P5** | AI 聊天界面 | 聊天 UI、上下文传递、产品推荐 | 4 天 |
| **P6** | 测试 + 演示 | 集成测试、演示脚本、PPT | 3 天 |

### 8.2 时间线（8-9 天）

#### Day 1-2: 环境搭建
- [ ] 创建 Spring Boot 项目
- [ ] 创建 MySQL 数据库
- [ ] 配置 OpenAI API
- [ ] 前端项目初始化
- [ ] 基础 API 测试

#### Day 3-5: 核心开发
- [ ] **P1**: RAG Service + PDF 向量化
- [ ] **P1**: Memory Service（三层记忆）
- [ ] **P2**: 8 个 Function 实现
- [ ] **P2**: Multi-Agent 编排
- [ ] **P3**: 数据库表创建 + 初始化数据
- [ ] **P3**: 编写 6 个产品 PDF 说明书
- [ ] **P4**: 产品列表和详情页
- [ ] **P4**: 订单页面（模拟支付）
- [ ] **P5**: AI 聊天界面
- [ ] **P5**: 上下文传递和产品推荐

#### Day 6-7: 集成测试
- [ ] **P6**: 前后端联调
- [ ] **P6**: 10 个演示场景测试
- [ ] **全员**: Bug 修复
- [ ] **P6**: 性能测试

#### Day 8-9: 演示准备
- [ ] **P6**: 演示数据准备
- [ ] **P6**: 演示脚本编写
- [ ] **全员**: 演示彩排
- [ ] **P6**: PPT 制作
- [ ] **P6**: 录制备用视频

---

## 9. 演示脚本

### 9.1 演示环境准备

**数据准备清单:**
- ✅ 6 个产品数据已录入
- ✅ 产品 PDF 说明书已向量化
- ✅ 演示订单 ORD-20260127-001 已创建
- ✅ 聊天会话已清空

**环境检查:**
```bash
# 后端
curl http://localhost:8080/api/products
# 应返回 6 个产品

# 向量存储
ls -lh ./data/vector-store.json
# 应存在且有数据

# 前端
curl http://localhost:5173
# 应正常访问
```

### 9.2 十大功能演示脚本

#### 1️⃣ Prompt Engineering（30 秒）
```
演示者: "你好，介绍一下你自己"

AI 回复: "您好，欢迎来到 Aura。我是您的专属礼宾员，致力于帮助您找到与生活共鸣的物品。Aura 秉承温暖的极简主义理念，将自然与科技完美融合。我可以为您介绍产品、查询订单、处理售后，请问需要什么帮助？"

✅ 展示点: 温暖、自然的品牌语气
```

#### 2️⃣ RAG（1 分钟）
```
演示者: "Aura Harmony 的蓝牙传输距离是多少？"

AI 回复: "Aura Harmony 采用蓝牙 5.3 技术，在开阔环境下传输距离可达 30 米，在室内环境约为 15 米。得益于自适应天线设计，即使隔墙也能保持稳定连接。（参考：产品说明书第 8 页）"

演示者: （打开 PDF 说明书第 8 页，展示确实有这个参数）

✅ 展示点: AI 从 PDF 中检索冷门参数，并注明来源
```

#### 3️⃣ Memory（1 分钟）
```
演示者: "我的订单 ORD-20260127-001 什么时候到？"

AI 回复: "您的订单 ORD-20260127-001 已发货，快递单号 SF1234567890，预计明天下午 15:00 前送达。包含 1 件 Aura Harmony 耳机。"

（等待 5 秒）

演示者: "我收到的这个颜色不对"

AI 回复: "非常抱歉给您带来不便。您订单 ORD-20260127-001 中的 Aura Harmony 耳机颜色有误吗？我可以立即为您申请换货或退款。请问您收到的是什么颜色？"

✅ 展示点: AI 从上下文中理解"这个"指的是 ORD-20260127-001 的耳机
```

#### 4️⃣ Function Calling（1 分钟）
```
演示者: "帮我看看订单状态"

（展示后端日志，显示调用了 getOrderStatus() 函数）

AI 回复: "您的订单 ORD-20260127-001 目前状态是「已发货」，快递单号 SF1234567890，预计明天 15:00 前送达。"

✅ 展示点: AI 自动从 Memory 提取订单号，调用接口查询
```

#### 5️⃣ Complex Task（1.5 分钟）
```
演示者: "我刚搬家了，帮我把刚才那个订单送到新地址：北京市朝阳区建国路 88 号 SOHO 现代城 A 座 1201"

（展示后端日志，显示多步骤执行）
1. 从 Memory 提取订单号: ORD-20260127-001
2. 提取新地址: 北京市朝阳区建国路 88 号...
3. 调用 updateOrderAddress() 函数
4. 检查订单状态（是否可修改）
5. 更新地址
6. 返回结果

AI 回复: "好的，我已经帮您将订单 ORD-20260127-001 的配送地址更新为「北京市朝阳区建国路 88 号 SOHO 现代城 A 座 1201」。由于订单尚未发货，地址修改已生效。"

演示者: （刷新订单页面，展示地址已更新）

✅ 展示点: 复杂任务的智能分解和执行
```

#### 6️⃣ Multi-Agent（1 分钟）
```
演示者: "推荐个耳机，顺便帮我查一下我的订单"

（展示后端日志，显示两个 Agent 协作）
- ProductExpertAgent: 处理产品推荐
- CustomerServiceAgent: 处理订单查询

AI 回复: "为您推荐 Aura Harmony 耳机，售价 429 元。它采用开放式声学设计，50 小时超长续航，非常适合日常通勤和办公使用。

另外，您的订单 ORD-20260127-001 已发货，预计明天下午送达。"

✅ 展示点: 多个 Agent 并行工作，结果整合
```

#### 7️⃣ Planning（1.5 分钟）
```
演示者: "我想给办公室配一套 Aura 的设备，预算 3000 元，需要照明、空气净化和音响"

（展示后端日志，显示规划过程）
1. 分析需求: 照明 + 空气净化 + 音响
2. 搜索相关产品
3. 计算组合方案
4. 检查预算
5. 推荐最优方案

AI 回复: "根据您的需求，我为您规划了以下方案：

**方案一（2877 元）**:
- Aura Beam 智能灯 × 2 = 498 元（提供全天候舒适照明）
- Aura Essence 空气净化器 = 599 元（净化空气 + 香薰）
- Aura Harmony 耳机 = 429 元（可作为桌面音响使用）
- Aura Canvas 平板 = 1099 元（作为控制中心）

剩余预算：123 元

这个组合可以通过 Aura Canvas 统一控制，营造舒适的办公环境。需要我帮您下单吗？"

✅ 展示点: 智能任务规划和多产品组合推荐
```

#### 8️⃣ Tool Chain（1 分钟）
```
演示者: "对比一下所有耳机的库存和价格"

（展示后端日志，显示工具链）
1. searchProducts(category="Audio")
2. getProductDetail(p1, p2, ...)
3. checkInventory(p1, p2, ...)
4. compareProducts(...)

AI 回复: "为您对比所有音频产品：

| 产品 | 价格 | 库存 | 特点 |
|------|------|------|------|
| Aura Harmony | 429 元 | 充足（50 件） | 50h 续航，开放式设计 |

目前我们只有一款音频产品 Aura Harmony，库存充足。如果您需要其他类型的产品，我可以为您推荐。"

✅ 展示点: 工具自动组合和数据聚合
```

#### 9️⃣ Error Handling（30 秒）
```
演示者: "查询订单 ORD-99999-999"

AI 回复: "抱歉，我没有找到订单号 ORD-99999-999。请检查订单号是否正确。您也可以告诉我您的邮箱或手机号，我帮您查询相关订单。"

✅ 展示点: 友好的错误处理和解决方案建议
```

#### 🔟 Reflection（1 分钟）
```
演示者: （对 AI 的某个回复不满意）"这个回答太简单了"

AI 内部: （自我反思）
- 分析用户反馈
- 评估原回复质量
- 生成改进版本

AI 回复: "您说得对，让我为您提供更详细的信息...（改进后的详细回复）"

✅ 展示点: AI 的自我评估和优化能力
```

### 9.3 演示时间分配

| 功能 | 时间 | 累计 |
|------|------|------|
| 开场介绍 | 1 分钟 | 1 分钟 |
| 1-2 (Prompt + RAG) | 1.5 分钟 | 2.5 分钟 |
| 3-4 (Memory + Function) | 2 分钟 | 4.5 分钟 |
| 5 (Complex Task) | 1.5 分钟 | 6 分钟 |
| 6-7 (Multi-Agent + Planning) | 2.5 分钟 | 8.5 分钟 |
| 8-10 (Tool Chain + Error + Reflection) | 2.5 分钟 | 11 分钟 |
| 总结 + Q&A | 4 分钟 | 15 分钟 |

---

## 10. 部署指南

### 10.1 本地开发环境

**系统要求:**
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

**启动步骤:**

```bash
# 1. 启动 MySQL
mysql -u root -p
CREATE DATABASE aura_db;

# 2. 启动后端
cd aura-backend
mvn spring-boot:run

# 3. 启动前端
cd aura-frontend
npm install
npm run dev
```

### 10.2 生产部署

**推荐架构:**
```
Nginx (反向代理)
├── Frontend (静态文件)
└── Backend (Spring Boot)
    ├── MySQL
    └── Vector Store (文件)
```

**Docker Compose 示例:**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: aura_db
    volumes:
      - mysql_data:/var/lib/mysql
  
  backend:
    build: ./aura-backend
    ports:
      - "8080:8080"
    environment:
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      - mysql
  
  frontend:
    build: ./aura-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

---

## 附录

### A. 常见问题

**Q1: SimpleVectorStore 能处理多少数据？**  
A: 建议 10,000 个文档以内。本项目 6 个产品说明书完全够用。

**Q2: 为什么不用真实支付？**  
A: 这是演示项目，重点在 AI 能力展示，支付集成会增加复杂度。

**Q3: 如何扩展到生产环境？**  
A: 添加用户认证、切换到 Chroma/Pinecone、集成真实支付网关。

### B. 参考资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 文档](https://platform.openai.com/docs)
- [React 官方文档](https://react.dev/)

### C. 联系方式

- 项目负责人: [姓名]
- 技术支持: [邮箱]

---

**版权声明**: © 2026 Aura Team. All Rights Reserved.
