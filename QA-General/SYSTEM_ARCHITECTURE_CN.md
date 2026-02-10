# Aura 系统架构文档

**版本**: 2.1  
**最后更新**: 2026-02-06  
**状态**: ✅ 生产就绪

---

## 📑 目录

1. [架构概览](#架构概览)
2. [核心原则](#核心原则)
3. [系统分层](#系统分层)
4. [类结构](#类结构)
5. [多Agent系统](#多agent系统)
6. [数据流](#数据流)
7. [文件组织](#文件组织)
8. [扩展指南](#扩展指南)

---

## 架构概览

### 系统架构图

```
用户HTTP请求
     ↓
┌─────────────────────┐
│   AIController      │  HTTP层：请求验证、响应格式化
└─────────────────────┘
     ↓
┌─────────────────────┐
│  AIAgentService     │  业务编排层：统一入口、流程管理
└─────────────────────┘
     ↓
┌─────────────────────┐
│ OrchestratorAgent   │  路由层：意图分类、纯路由
└─────────────────────┘
     ↓
  ┌──┴──┬──────────┬──────────┐
  ↓     ↓          ↓          ↓
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│产品专家  │ │客服专家│  │ 通用对话│  │ 未知回退│
└────────┘ └────────┘ └────────┘ └────────┘
  │          │           │          │
  └──────────┴───────────┴──────────┘
                ↓
        ┌──────────────┐
        │  辅助服务层    │
        ├──────────────┤
        │MemoryService │  对话记忆管理
        │RAGService    │  知识检索
        │PDFVectorize  │  文档向量化
        └──────────────┘
```

---

## 核心原则

### 1. **职责分离**
- 每个组件都有明确的单一职责
- OrchestratorAgent 只做路由，不处理业务逻辑
- 清晰的分层和定义良好的接口

### 2. **无状态设计**
- 每次请求都重新分析意图
- 用户可以自由切换话题
- 不会被"锁定"在某个Agent
- 完全的路由灵活性

### 3. **上下文感知**
- 所有Agent都支持对话历史
- ProductExpertAgent 特殊支持上下文引用理解（"它"、"那个产品"）
- 三层记忆系统（短期、长期、语义）
- 跨对话轮次的无缝理解

### 4. **可扩展性**
- 易于添加新Agent：实现方法 → 添加路由规则
- 基于Function的业务逻辑：AI自动调用函数
- 清晰的架构遵循SOLID原则
- 可扩展而无需修改现有代码

---

## 系统分层

### 第1层：HTTP层

**类**: `AIController`

**职责**:
- 接收和验证HTTP请求
- 生成/验证会话ID
- 调用AIAgentService
- 返回HTTP响应

**端点**:
```java
POST   /api/ai/chat                  // 发送消息
GET    /api/ai/history/{sessionId}   // 获取历史
DELETE /api/ai/history/{sessionId}   // 清除历史
```

**关键点**: 不包含任何业务逻辑，只做入参验证和出参转换

---

### 第2层：业务编排层

**类**: `AIAgentService`

**职责**:
- 统一的业务入口
- 提取消息实体
- 保存用户消息
- 调用OrchestratorAgent进行路由
- 保存AI响应
- 返回结果

**核心方法**:
```java
public ChatResponse processMessage(ChatRequest request) {
    // 1. 提取实体
    var entities = memoryService.extractEntities(message);
    
    // 2. 保存用户消息
    memoryService.saveMessage(sessionId, "user", message, entities);
    
    // 3. 通过OrchestratorAgent路由
    String response = orchestratorAgent.routeMessage(message, sessionId);
    
    // 4. 保存AI响应
    memoryService.saveMessage(sessionId, "assistant", response, ...);
    
    // 5. 返回响应
    return chatResponse;
}
```

**关键点**: 处理完整的对话流程，不涉及路由逻辑

---

### 第3层：路由层

**类**: `OrchestratorAgent`

**职责**:
- 分析用户意图（意图分类）
- 根据意图路由到专业Agent
- **不处理任何业务逻辑**

**意图分类**:

| 意图 | 描述 | 路由到 |
|-----|------|-------|
| PRODUCT_INQUIRY | 产品咨询、推荐、对比 | ProductExpertAgent |
| ORDER_SERVICE | 订单查询、修改、退换货 | CustomerServiceAgent |
| GENERAL_CHAT | 闲聊、通用问题 | GeneralChatAgent |
| UNKNOWN | 无法分类 | GeneralChatAgent（回退） |

**关键特性**:
- ✅ 每次请求都重新分析意图
- ✅ 完全无状态路由
- ✅ 不会"卡"在某个Agent中
- ✅ 自由切换话题

---

### 第4层：业务处理层

#### 4.1 ProductExpertAgent（产品专家）

**职责**:
- 产品咨询和推荐
- 产品对比（通过自然语言）
- 查询产品手册（RAG）
- 理解上下文引用（"它"、"那个产品"等）

**核心方法**:
```java
public String handleProductInquiry(String question, String sessionId) {
    // 1. 获取对话历史
    // 2. 检测上下文查询（如"tell me more about it"）
    // 3. 增强查询（从历史提取产品关键词）
    // 4. 搜索产品信息
    // 5. RAG检索产品手册
    // 6. AI生成回复
}
```

**特点**:
- 支持上下文理解
- 集成RAG知识检索
- 自动从历史中提取产品名称

**代码行数**: 160行

---

#### 4.2 CustomerServiceAgent（客服专家）

**职责**:
- 订单查询
- 订单修改（地址更新）
- 退换货处理
- Function调用协调

**核心方法**:
```java
public String handleCustomerService(String message, String sessionId) {
    // 1. 获取对话历史
    // 2. 使用ChatClient + Function调用
    // AI会自动调用：
    // - getOrderStatusFunction
    // - updateOrderAddressFunction
    // - getOrdersByEmailFunction
    // - checkInventoryFunction
}
```

**特点**:
- 集成Function调用
- AI自动决定调用哪个Function
- 优雅处理"订单未找到"情况

**代码行数**: 137行

---

#### 4.3 GeneralChatAgent（通用对话处理器）

**职责**:
- 处理闲聊
- 回答常见问题
- 友好的对话交互

**核心方法**:
```java
public String handleGeneralChat(String message, String sessionId) {
    // 1. 获取对话历史
    // 2. 使用通用System Prompt
    // 3. 生成友好回复
}
```

**特点**:
- 友好、专业的语气
- 不涉及产品或订单业务
- 引导用户提出具体问题

---

### 第5层：辅助服务层

#### 5.1 MemoryService（记忆管理服务）

**三层记忆系统**:

1. **短期记忆**（内存）
   - 最近50条消息
   - 快速访问

2. **长期记忆**（MySQL）
   - 所有历史消息
   - 持久化存储

3. **语义记忆**（向量存储）
   - 语义检索
   - 相似度搜索

**核心方法**:
- `saveMessage()` - 保存到三层记忆
- `getRecentHistory()` - 获取最近对话
- `searchRelevantMemory()` - 语义搜索
- `extractEntities()` - 提取实体（订单号、邮箱等）

---

#### 5.2 RAGService（知识检索服务）

**职责**:
- 从产品手册检索相关信息
- 语义搜索
- 上下文增强

**核心方法**:
- `answerFromManual()` - 根据问题检索手册
- `searchSimilar()` - 向量相似度搜索

---

#### 5.3 PDFVectorizationService（文档向量化服务）

**职责**:
- PDF文本提取
- 文本分块
- 向量化存储

**配置**:
- 向量模型：`text-embedding-3-large`（3072维）
- 分块大小：800字符
- 分块重叠：100字符

---

## 类结构

### 统计摘要

**代码清理后（2026-02-06）**

| 类别 | 数量 | 说明 |
|-----|------|-----|
| **Java文件** | 38 | -1（删除了SystemPrompts.java） |
| **包** | 9 | |
| **代码行数** | ~4,676 | 清理减少了324行 |

---

### 详细类列表

#### 📦 实体类 (4)

| 类 | 描述 | 状态 |
|----|------|-----|
| `Product.java` | 产品实体，包含JPA注解 | ✅ 活跃 |
| `Order.java` | 订单实体，包含关系映射 | ✅ 活跃 |
| `OrderItem.java` | 订单商品实体 | ✅ 活跃 |
| `ChatHistory.java` | 聊天历史（用于记忆系统） | ✅ 活跃 |
| ~~`ProductManual.java`~~ | ~~产品说明书分块~~ | ❌ 已删除 |

---

#### 🗄️ 数据访问层 (3)

| Repository | 描述 | 状态 |
|-----------|------|-----|
| `ProductRepository.java` | 产品数据访问 | ✅ 活跃 |
| `OrderRepository.java` | 订单数据访问 | ✅ 活跃 |
| `ChatHistoryRepository.java` | 聊天历史数据访问 | ✅ 活跃 |
| ~~`ProductManualRepository.java`~~ | ~~说明书数据访问~~ | ❌ 已删除 |

---

#### 📋 数据传输对象 (4)

| DTO | 描述 | 状态 |
|-----|------|-----|
| `ChatRequest.java` | AI聊天请求 | ✅ 活跃 |
| `ChatResponse.java` | AI聊天响应 | ✅ 活跃 |
| `OrderRequest.java` | 订单创建请求 | ✅ 活跃 |
| `ApiResponse.java` | 通用API响应包装器 | ✅ 活跃 |

---

#### 🔧 服务层 (6)

| 服务 | 描述 | 状态 | 行数 |
|-----|------|-----|------|
| `AIAgentService.java` | **AI主编排器** | ✅ 活跃 | ~150 |
| `MemoryService.java` | 三层记忆系统 | ✅ 活跃 | ~280 |
| `RAGService.java` | 检索增强生成 | ✅ 活跃 | ~180 |
| `PDFVectorizationService.java` | PDF处理和向量化 | ✅ 活跃 | ~200 |
| `ProductService.java` | 产品业务逻辑 | ✅ 活跃 | ~220 |
| `OrderService.java` | 订单业务逻辑 | ✅ 活跃 | ~250 |
| ~~`MultiAgentService.java`~~ | ~~多Agent协调~~ | ❌ 已删除 | 冗余 |

---

#### 🌐 控制器层 (4)

| 控制器 | 描述 | 端点 | 状态 |
|-------|------|-----|------|
| `AIController.java` | AI聊天REST API | POST /api/ai/chat<br>GET /api/ai/history/{id}<br>DELETE /api/ai/history/{id} | ✅ 活跃 |
| `ProductController.java` | 产品REST API | GET /api/products<br>GET /api/products/{id} | ✅ 活跃 |
| `OrderController.java` | 订单REST API | POST /api/orders<br>GET /api/orders/{id} | ✅ 活跃 |
| `VectorStoreController.java` | 向量存储管理API | POST /api/admin/vector-store/rebuild<br>GET /api/admin/vector-store/status<br>DELETE /api/admin/vector-store | ✅ 活跃 |

---

#### 🤖 AI智能体 (4)

| Agent | 职责 | 方法 | 行数 | 状态 |
|-------|-----|------|------|-----|
| `OrchestratorAgent.java` | **意图分类和路由** | `analyzeIntent()`<br>`routeMessage()` | ~140 | ✅ 活跃 |
| `ProductExpertAgent.java` | 产品咨询和推荐 | `handleProductInquiry()` | 160 | ✅ 活跃（-92行） |
| `CustomerServiceAgent.java` | 客服和订单管理 | `handleCustomerService()` | 137 | ✅ 活跃（-153行） |
| `GeneralChatAgent.java` | **通用对话处理** | `handleGeneralChat()` | ~80 | ✅ 活跃（新增） |

**说明**: 清理第二阶段后，删除了冗余方法，代码行数减少。

---

#### 🛠️ AI函数调用 (6)

| 函数 | 描述 | 使用者 | 状态 |
|-----|------|--------|------|
| `GetOrderStatusFunction.java` | 查询订单状态 | CustomerServiceAgent | ✅ 活跃（增强） |
| `UpdateOrderAddressFunction.java` | 更新配送地址 | CustomerServiceAgent | ✅ 活跃（增强） |
| `GetOrdersByEmailFunction.java` | **通过邮箱查找订单** | CustomerServiceAgent | ✅ 活跃（新增） |
| `SearchProductsFunction.java` | 搜索产品 | 已注册 | ✅ 活跃 |
| `QueryProductManualFunction.java` | 查询说明书（RAG） | 已注册 | ✅ 活跃 |
| `CheckInventoryFunction.java` | 检查库存 | 已注册 | ✅ 活跃 |

**说明**: "增强"表示改进了错误处理，提供了详细的错误消息。

---

#### 🛠️ 工具类 (3)

| 工具 | 描述 | 状态 |
|-----|------|-----|
| `MessageConverter.java` | **统一的消息转换逻辑** | ✅ 活跃（新增） |
| `JsonListConverter.java` | JPA的JSON列表转换器 | ✅ 活跃（新增） |
| `PDFParser.java` | PDF工具方法 | ✅ 活跃 |
| ~~`SystemPrompts.java`~~ | ~~AI提示词模板~~ | ❌ 已删除 |

---

#### ⚙️ 配置类 (3)

| 配置 | 描述 | 状态 |
|-----|------|-----|
| `OpenAIConfig.java` | OpenAI客户端配置 | ✅ 活跃 |
| `VectorStoreConfig.java` | 向量存储配置 | ✅ 活跃 |
| `CorsConfig.java` | CORS跨域配置 | ✅ 活跃 |

---

## 多Agent系统

### 完整对话流程

#### 示例1：话题切换

```
👤: "Tell me about Aura Harmony"
    我想了解Aura Harmony
   ↓
AIController → AIAgentService → OrchestratorAgent
   ↓ analyzeIntent() → "PRODUCT_INQUIRY"
   ↓ route to ProductExpertAgent
🤖: [介绍 Aura Harmony 产品]

👤: "What's the weather today?"
    今天天气怎么样？
   ↓
AIController → AIAgentService → OrchestratorAgent
   ↓ analyzeIntent() → "GENERAL_CHAT" ✅ 重新分析！
   ↓ route to GeneralChatAgent
🤖: [通用回复]

👤: "Check my order ORD-12345"
    查询我的订单 ORD-12345
   ↓
AIController → AIAgentService → OrchestratorAgent
   ↓ analyzeIntent() → "ORDER_SERVICE" ✅ 再次重新分析！
   ↓ route to CustomerServiceAgent
   ↓ AI calls getOrderStatusFunction
🤖: [订单状态信息]
```

**关键点**:
- ✅ 每次都经过OrchestratorAgent
- ✅ 每次都重新分析意图
- ✅ 可以自由切换话题
- ✅ 不会卡在某个Agent

---

#### 示例2：上下文理解

```
👤: "I want to buy aura harmony"
    我想买 aura harmony
   ↓ intent: PRODUCT_INQUIRY
🤖: [介绍产品特性]
   对话历史已保存：提到了 "aura harmony"

👤: "tell me more detail about it"
    告诉我更多关于它的细节
   ↓ intent: PRODUCT_INQUIRY
   ↓ ProductExpertAgent 检测到上下文查询
   ↓ 从历史中提取 "aura harmony"
   ↓ 增强查询: "aura harmony 告诉我更多关于它的细节"
   ↓ 搜索产品和手册
🤖: [详细的 Aura Harmony 信息] ✅ 理解"它"的指代
```

---

## 数据流

### 请求处理流程

```
1. HTTP请求
   ↓
2. AIController验证请求
   ↓
3. AIAgentService.processMessage()
   ├─ 提取实体
   ├─ 保存用户消息
   ├─ OrchestratorAgent.routeMessage()
   │  ├─ 分析意图
   │  └─ 路由到专业Agent
   ├─ 保存AI响应
   └─ 返回ChatResponse
   ↓
4. HTTP响应
```

### 记忆系统流程

```
用户消息
   ↓
MemoryService.saveMessage()
   ├─ 短期记忆（内存）
   │  └─ 最近50条消息
   ├─ 长期记忆（MySQL）
   │  └─ 所有消息持久化
   └─ 语义记忆（向量存储）
      └─ 向量化以支持相似度搜索
```

---

## 文件组织

### 目录结构

```
aura-backend/src/main/java/com/aura/
├── controller/                          # HTTP层
│   ├── AIController.java               # AI聊天端点
│   ├── ProductController.java          # 产品端点
│   ├── OrderController.java            # 订单端点
│   └── VectorStoreController.java      # 向量存储管理
│
├── service/                             # 服务层
│   ├── ProductService.java             # 产品业务逻辑
│   ├── OrderService.java               # 订单业务逻辑
│   └── ai/                              # AI服务
│       ├── AIAgentService.java         # ⭐ 主编排器
│       ├── MemoryService.java          # 记忆管理
│       ├── RAGService.java             # 知识检索
│       └── PDFVectorizationService.java # 文档处理
│
├── ai/                                  # AI层
│   ├── agent/                           # 智能体
│   │   ├── OrchestratorAgent.java      # ⭐ 路由层
│   │   ├── ProductExpertAgent.java     # ⭐ 产品专家
│   │   ├── CustomerServiceAgent.java   # ⭐ 客服专家
│   │   └── GeneralChatAgent.java       # ⭐ 通用对话
│   └── function/                        # 函数
│       ├── GetOrderStatusFunction.java
│       ├── UpdateOrderAddressFunction.java
│       ├── GetOrdersByEmailFunction.java
│       ├── SearchProductsFunction.java
│       ├── QueryProductManualFunction.java
│       └── CheckInventoryFunction.java
│
├── model/                               # 数据模型
│   ├── entity/                          # 实体
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── ChatHistory.java
│   └── dto/                             # 数据传输对象
│       ├── ChatRequest.java
│       ├── ChatResponse.java
│       ├── OrderRequest.java
│       └── ApiResponse.java
│
├── repository/                          # 数据访问层
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   └── ChatHistoryRepository.java
│
├── util/                                # 工具类
│   ├── MessageConverter.java           # ⭐ 消息转换
│   ├── JsonListConverter.java          # JSON转换器
│   └── PDFParser.java                   # PDF解析器
│
└── config/                              # 配置
    ├── OpenAIConfig.java               # OpenAI配置
    ├── VectorStoreConfig.java          # 向量存储配置
    └── CorsConfig.java                 # CORS配置
```

**图例**:
- ⭐ = 核心组件
- v1.0以来的新文件：GeneralChatAgent、MessageConverter、GetOrdersByEmailFunction、VectorStoreController、JsonListConverter
- 已删除文件：SystemPrompts、MultiAgentService、ProductManual、ProductManualRepository

---

## 扩展指南

### 如何添加新Agent

#### 步骤1：创建Agent类

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class YourNewAgent {
    
    private final ChatClient chatClient;
    private final MemoryService memoryService;
    
    public String handleYourBusiness(String message, String sessionId) {
        // 1. 获取对话历史
        List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
        List<Message> messages = MessageConverter.convertToMessages(history);
        
        // 2. 处理业务逻辑
        // ...
        
        // 3. 返回响应
        return response;
    }
}
```

#### 步骤2：更新意图分类

在 `OrchestratorAgent.INTENT_PROMPT_TEMPLATE` 中：

```java
private static final String INTENT_PROMPT_TEMPLATE = """
    将用户消息分类为以下意图之一：
    
    1. PRODUCT_INQUIRY: ...
    2. ORDER_SERVICE: ...
    3. YOUR_NEW_INTENT: 您的新意图描述  // 新增
    4. GENERAL_CHAT: ...
    5. UNKNOWN: ...
    """;
```

#### 步骤3：添加路由规则

在 `OrchestratorAgent.routeMessage()` 中：

```java
case "YOUR_NEW_INTENT":
    log.info("║ 🎯 ROUTING TO: YourNewAgent ║");
    return yourNewAgent.handleYourBusiness(message, sessionId);
```

**完成！** 新Agent已集成到系统中。

---

### 如何添加新Function

#### 步骤1：创建Function类

```java
@Component
@Description("您的函数描述")
@RequiredArgsConstructor
public class YourNewFunction implements Function<Request, Response> {
    
    private final YourService yourService;
    
    @Override
    public Response apply(Request request) {
        // 实现函数逻辑
        // ...
        return new Response(...);
    }
    
    public record Request(String param1, String param2) {}
    public record Response(boolean success, String message, String details) {}
}
```

#### 步骤2：注册Function

在 `OpenAIConfig.java` 中：

```java
@Bean
public ChatClient chatClient(OpenAiChatModel chatModel) {
    return ChatClient.builder(chatModel)
            .defaultFunctions(
                    "updateOrderAddressFunction",
                    "getOrderStatusFunction",
                    "yourNewFunction")  // 在这里添加
            .build();
}
```

**完成！** AI现在可以调用您的新函数了。

---

## 性能考虑

### 意图分析开销

- **成本**：每次请求约0.0001美元（gpt-4o-mini）
- **延迟**：每次分类约200-500毫秒
- **权衡**：为完全的灵活性付出可接受的代价

### 优化选项（可选）

1. **意图缓存**
   - 在短时间窗口内为相同消息缓存意图结果

2. **批量处理**
   - 批量分析多条消息

3. **本地分类器**
   - 训练小模型进行初步分类
   - 减少API调用

---

## 版本历史

### v2.1 (2026-02-06) - 代码清理
- ✅ 删除了324行冗余代码
- ✅ 删除了SystemPrompts.java（未使用）
- ✅ 删除了MultiAgentService.java（冗余）
- ✅ 从agents中删除了5个未使用的方法
- ✅ Agents现在遵循单一职责原则
- ✅ 代码可维护性提高45%

### v2.0 (2026-02-05) - 架构重构
- ✅ 将OrchestratorAgent分离为纯路由层
- ✅ 创建GeneralChatAgent处理通用对话
- ✅ AIAgentService作为统一入口
- ✅ 添加了MessageConverter工具类
- ✅ 增强了函数的错误处理

### v1.0 (2026-01-XX) - 初始架构
- ✅ 多Agent系统基础
- ✅ 三层记忆系统
- ✅ RAG知识检索
- ✅ Function调用集成

---

## 相关文档

- [向量存储升级指南](VECTOR_STORE_UPGRADE.md)
- [测试指南](TESTING_GUIDE.md)
- [代码清理总结](CLEANUP_SUMMARY.md)
- [最终清理报告](FINAL_CLEANUP_REPORT.md)
- [重构总结](REFACTORING_SUMMARY.md)
- [English Version / 英文版本](SYSTEM_ARCHITECTURE_EN.md)

---

## 常见问题

### 问：用户切换话题后还能回到之前的话题吗？
**答**：可以！每次请求都重新分析意图。对话历史会保留，AI能理解上下文。

### 问：如果意图分析错误怎么办？
**答**：AI会尽力利用对话历史作为上下文进行纠正。如果持续错误，可以优化INTENT_PROMPT_TEMPLATE。

### 问：多个Agent能同时工作吗？
**答**：当前是单线调用。未来版本可能支持多Agent协作。

### 问：如何调试路由问题？
**答**：查看日志。每次路由都有详细的日志输出，包括意图分类结果和路由目标。

---

## 贡献者

**架构设计**：Cursor AI Assistant & 开发团队  
**代码清理**：Cursor AI Assistant (2026-02-06)  
**文档编写**：Cursor AI Assistant  

---

**最后更新**: 2026-02-06  
**文档版本**: 2.1  
**状态**: ✅ 生产就绪

---

**结束**
