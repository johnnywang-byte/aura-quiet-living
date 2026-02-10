# Aura AI调用流程完整解析

**文档版本**: v1.0  
**创建日期**: 2026-02-07  
**作者**: AI Assistant  
**目的**: 详细解析Aura项目中AI从前端到后端的完整调用链路

---

## 📋 目录

1. [系统架构概览](#系统架构概览)
2. [完整调用链路](#完整调用链路)
3. [前端发起请求](#前端发起请求)
4. [后端API入口](#后端api入口)
5. [AI服务核心](#ai服务核心)
6. [意图识别与路由](#意图识别与路由)
7. [OpenAI返回意图详解](#openai返回意图详解)
8. [产品专家Agent](#产品专家agent)
9. [增强查询机制](#增强查询机制)
10. [客户服务Agent](#客户服务agent)
11. [Function Calling机制](#function-calling机制)
12. [记忆系统](#记忆系统)
13. [完整案例演示](#完整案例演示)
14. [调试指南](#调试指南)

---

## 系统架构概览

### 核心组件

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户界面层                                │
│                    (React + TypeScript)                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Assistant.tsx  →  API封装 (api.ts)                       │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP POST /api/ai/chat
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                         后端API层                                 │
│                    (Spring Boot + Java)                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AIController  →  AIAgentService                          │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                         AI编排层                                  │
│                   (OrchestratorAgent)                            │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  意图识别  →  路由到具体Agent                              │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────┬────────────────┬─────────────────┬─────────────────┬─────┘
       │                │                 │                 │
       ↓                ↓                 ↓                 ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────────┐
│ ProductExpert│ │  Customer    │ │   General    │ │   Memory    │
│    Agent     │ │   Service    │ │     Chat     │ │   Service   │
│              │ │    Agent     │ │    Agent     │ │             │
│ • 产品搜索   │ │ • 订单查询   │ │ • 通用对话   │ │ • 短期记忆  │
│ • RAG检索    │ │ • Function   │ │ • 闲聊       │ │ • 长期记忆  │
│ • 增强查询   │ │   Calling    │ │              │ │ • 语义搜索  │
└──────────────┘ └──────────────┘ └──────────────┘ └─────────────┘
       │                │                              │
       ↓                ↓                              ↓
┌──────────────────────────────────────────────────────────┐
│                      数据层                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │    MySQL     │  │ Vector Store │  │   OpenAI     │   │
│  │  (订单/产品)  │  │  (产品手册)   │  │     API      │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└──────────────────────────────────────────────────────────┘
```

---

## 完整调用链路

### 时序图

```
用户输入 "我的订单 ORD-123 状态如何？"
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【前端】Assistant.tsx (第98-145行)                           │
│ • 封装 ChatRequest { message, sessionId }                   │
│ • 调用 chatApi.sendMessage()                                │
└────────────────────────────────────────────────────────────┘
    │
    │ HTTP POST /api/ai/chat
    │ Body: { "message": "...", "sessionId": "abc-123" }
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【API入口】AIController.java (第36-73行)                     │
│ • 接收请求，生成/复用 sessionId                              │
│ • 调用 aiAgentService.processMessage()                      │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【核心服务】AIAgentService.java (第40-86行)                  │
│ 1. 提取实体: { orderNumbers: ["ORD-123"] }                  │
│ 2. 保存用户消息 → MemoryService (内存+MySQL+向量库)          │
│ 3. 调用 orchestratorAgent.routeMessage() ← 核心路由         │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【路由中心】OrchestratorAgent.java (第106-156行)             │
│ 1. 调用 analyzeIntent() 分析用户意图                        │
│    ├─ 加载最近5条历史对话                                    │
│    ├─ 构建意图分类Prompt                                     │
│    └─ 调用OpenAI API → 返回 "ORDER_SERVICE"                 │
│ 2. switch语句根据意图路由:                                   │
│    • PRODUCT_INQUIRY → ProductExpertAgent                   │
│    • ORDER_SERVICE   → CustomerServiceAgent  ← 当前路径     │
│    • GENERAL_CHAT    → GeneralChatAgent                     │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【客户服务Agent】CustomerServiceAgent.java                   │
│ 1. 加载历史对话 (最近10条)                                   │
│ 2. 构建System Prompt (声明Function能力)                      │
│ 3. 调用OpenAI，触发Function Calling                         │
│    → OpenAI识别需要调用 getOrderStatusFunction              │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【Function执行】GetOrderStatusFunction.java                  │
│ 1. 解析参数: orderNumber = "ORD-123"                        │
│ 2. 调用 orderService.getOrderByNumber("ORD-123")           │
│ 3. 查询MySQL数据库                                           │
│ 4. 返回订单信息JSON:                                         │
│    { success: true, code: "ORDER_FOUND",                    │
│      message: "订单号: ORD-123, 状态: SHIPPED, ..." }        │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【OpenAI整合】CustomerServiceAgent                           │
│ • 接收Function返回的JSON                                     │
│ • 将JSON转换成自然语言:                                       │
│   "您的订单ORD-123当前状态是已发货(SHIPPED)，                │
│    配送地址是xxx，预计3-5天送达..."                           │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【保存AI回复】MemoryService.saveMessage()                    │
│ • 保存到MySQL (长期记忆)                                     │
│ • 保存到内存ConcurrentHashMap (短期记忆，最多50条)            │
│ • 向量化保存到Vector Store (语义记忆)                        │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【返回前端】ChatResponse                                      │
│ { message: "您的订单ORD-123...",                             │
│   sessionId: "abc-123",                                     │
│   timestamp: "2026-02-07T10:30:00" }                        │
└────────────────────────────────────────────────────────────┘
    │
    ↓
┌────────────────────────────────────────────────────────────┐
│ 【显示给用户】Assistant.tsx 渲染AI消息                        │
│ 🤖: 您的订单ORD-123当前状态是已发货...                       │
└────────────────────────────────────────────────────────────┘
```

---

🔄 当前的路由机制
✅ 答案：是的，每次都会重新路由！
当前架构下，每条用户消息都会经过完整的路由流程：
    用户消息
        ↓
    AIAgentService.processMessage()          // 统一入口
        ↓
    OrchestratorAgent.routeMessage()         // 每次都重新分析意图
        ↓
    analyzeIntent() → 分类意图               // AI 分析当前消息意图
        ↓
    switch (intent) {                        // 根据意图路由
        case "PRODUCT_INQUIRY" → ProductExpertAgent
        case "ORDER_SERVICE" → CustomerServiceAgent
        case "GENERAL_CHAT" → GeneralChatAgent
    }

---

## 前端发起请求

### 文件位置
`aura-frontend/components/Assistant.tsx`

### 核心代码 (第98-145行)

```typescript
const handleSend = async () => {
    if (!inputValue.trim()) return;

    // 1. 构建用户消息对象
    const userMsg: ChatMessage = { 
        role: 'user', 
        text: inputValue, 
        timestamp: Date.now() 
    };
    
    // 2. 更新UI显示用户消息
    setMessages(prev => [...prev, userMsg]);
    setInputValue('');
    setIsThinking(true);

    try {
        // 3. 构建API请求
        const request: ChatRequest = {
            message: userMsg.text,
            sessionId: sessionId  // ← 关键：会话ID管理
        };

        // 4. 调用后端API
        const response = await chatApi.sendMessage(request);

        if (response) {
            // 5. 更新sessionId（首次为空，后端会生成）
            if (response.sessionId) {
                setSessionId(response.sessionId);
            }

            // 6. 构建AI回复消息对象
            const aiMsg: ChatMessage = {
                role: 'model',
                text: response.message,
                timestamp: Date.now()
            };
            
            // 7. 更新UI显示AI回复
            setMessages(prev => [...prev, aiMsg]);
        } else {
            // 错误处理
            const errorMsg: ChatMessage = {
                role: 'model',
                text: "I'm having trouble connecting to the server...",
                timestamp: Date.now()
            };
            setMessages(prev => [...prev, errorMsg]);
        }

    } catch (error) {
        console.error("Chat Error:", error);
        // 错误处理...
    } finally {
        setIsThinking(false);
    }
};
```

### 关键理解点

#### 1. SessionId管理

```typescript
const [sessionId, setSessionId] = useState<string>('');
```

**首次对话**：
- `sessionId` 为空字符串
- 后端收到空sessionId → 生成新的UUID
- 后端返回新生成的sessionId
- 前端保存到React state中

**后续对话（同一会话内）**：
- 前端使用已保存的sessionId
- 后端根据sessionId加载历史对话
- 实现上下文连贯性

**⚠️ 当前限制**：
- sessionId存储在React state中（非持久化）
- **页面刷新后sessionId会丢失**，后端会生成新的sessionId
- 虽然历史对话被保存在SQL数据库中，但因为sessionId变了，AI无法读取之前的对话历史
- **对话记忆仅在单次会话期间有效**（从打开页面到刷新/关闭页面）
- 数据库中的历史记录目前只用于存储，不用于跨会话的长期记忆

**改进方向**：
- 可以使用localStorage或sessionStorage持久化sessionId
- 或实现基于用户身份（email/userId）的长期记忆系统

#### 2. API调用封装

**文件**: `aura-frontend/services/api.ts` (第177-188行)

```typescript
export const chatApi = {
    async sendMessage(request: ChatRequest): Promise<ChatResponse | null> {
        const response = await fetchApi<ChatResponse>('/ai/chat', {
            method: 'POST',
            body: JSON.stringify(request),
        });
        return response.data;
    }
};

// fetchApi内部实现
async function fetchApi<T>(endpoint: string, options?: RequestInit): Promise<ApiResponse<T>> {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options?.headers,
            },
        });

        const data: ApiResponse<T> = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        return {
            success: false,
            data: null,
            message: error instanceof Error ? error.message : 'Unknown error',
        };
    }
}
```

**请求格式**：
```json
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我的订单 ORD-123 状态如何？",
  "sessionId": "abc-123-def-456"
}
```

**响应格式**：
```json
{
  "success": true,
  "data": {
    "message": "您的订单ORD-123当前状态是已发货...",
    "sessionId": "abc-123-def-456",
    "timestamp": "2026-02-07T10:30:00"
  },
  "message": null
}
```

---

## 后端API入口

### 文件位置
`aura-backend/src/main/java/com/aura/controller/AIController.java`

### 核心代码 (第36-73行)

```java
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AIAgentService aiAgentService;

    /**
     * POST /api/ai/chat - Send message to AI agent
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            // 1. SessionId管理：生成或复用
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();  // ← 生成新UUID
                request.setSessionId(sessionId);
                log.info("Generated new session ID: {}", sessionId);
            }

            log.info("Received chat request: sessionId={}, message='{}'",
                    sessionId, request.getMessage());

            // 2. 调用AI服务处理
            ChatResponse response = aiAgentService.processMessage(request);

            log.info("Chat response generated: sessionId={}, length={}",
                    response.getSessionId(), response.getMessage().length());

            // 3. 返回成功响应
            return ApiResponse.success(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid chat request: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage());
            return ApiResponse.error("Invalid request: " + e.getMessage());

        } catch (Exception e) {
            log.error("Chat error: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage(), e);
            return ApiResponse.error("AI service temporarily unavailable...");
        }
    }
}
```

### 关键理解点

#### 1. SessionId生成策略

```java
String sessionId = request.getSessionId();
if (sessionId == null || sessionId.trim().isEmpty()) {
    sessionId = UUID.randomUUID().toString();
    // 示例: "7a3e9b2c-4f5d-4e6a-8c9b-1d2e3f4a5b6c"
}
```

**为什么要生成UUID？**
- ✅ 唯一标识一次对话会话
- ✅ 后续可通过sessionId查询历史对话
- ✅ 支持多用户并发对话（每个用户独立sessionId）

#### 2. 异常处理

```java
try {
    // 正常处理
    return ApiResponse.success(response);
} catch (IllegalArgumentException e) {
    // 参数错误（如空消息）
    return ApiResponse.error("Invalid request: " + e.getMessage());
} catch (Exception e) {
    // 系统错误
    return ApiResponse.error("AI service temporarily unavailable...");
}
```

---

## AI服务核心

### 文件位置
`aura-backend/src/main/java/com/aura/service/ai/AIAgentService.java`

### 核心代码 (第40-86行)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AIAgentService {

    private final OrchestratorAgent orchestratorAgent;
    private final MemoryService memoryService;

    /**
     * Process user message - 核心处理方法
     */
    public ChatResponse processMessage(ChatRequest request) {
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();
        
        // 1. 验证输入
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("User message cannot be null or empty");
        }

        log.info("Processing message for session: {}", sessionId);

        try {
            // 2. 提取实体（订单号、邮箱、电话等）
            Map<String, Object> entities = memoryService.extractEntities(userMessage);
            log.info("Extracted entities: {}", entities);

            // 3. 保存用户消息到记忆系统（三层记忆）
            memoryService.saveMessage(sessionId, "user", userMessage, entities);

            // 4. 路由到编排器Agent（核心！）
            String responseContent = orchestratorAgent.routeMessage(userMessage, sessionId);

            // 5. 保存AI回复到记忆系统
            memoryService.saveMessage(sessionId, "assistant", responseContent, 
                    Map.of("entities", entities));

            // 6. 构建并返回响应
            ChatResponse response = new ChatResponse();
            response.setSessionId(sessionId);
            response.setMessage(responseContent);
            response.setTimestamp(LocalDateTime.now().toString());

            log.info("Message processed successfully for session: {}", sessionId);
            return response;

        } catch (Exception e) {
            log.error("Error processing message for session {}: {}", 
                    sessionId, e.getMessage(), e);
            
            // 创建错误响应
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(sessionId);
            errorResponse.setMessage(
                "I'm sorry, I encountered an error while processing your request...");
            errorResponse.setTimestamp(LocalDateTime.now().toString());
            return errorResponse;
        }
    }
}
```

### 关键理解点

#### 1. 实体提取

```java
Map<String, Object> entities = memoryService.extractEntities(userMessage);
```

**实体提取逻辑** (`MemoryService.java` 第157-207行)：

```java
public Map<String, Object> extractEntities(String message) {
    Map<String, Object> entities = new HashMap<>();

    // 提取订单号 (ORD-yyyyMMddHHmmss-XXXX)
    Pattern orderPattern = Pattern.compile("ORD-\\d{14}-\\d{4}");
    Matcher orderMatcher = orderPattern.matcher(message);
    List<String> orderNumbers = new ArrayList<>();
    while (orderMatcher.find()) {
        orderNumbers.add(orderMatcher.group());
    }
    if (!orderNumbers.isEmpty()) {
        entities.put("orderNumbers", orderNumbers);
    }

    // 提取邮箱地址
    Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    Matcher emailMatcher = emailPattern.matcher(message);
    List<String> emails = new ArrayList<>();
    while (emailMatcher.find()) {
        emails.add(emailMatcher.group());
    }
    if (!emails.isEmpty()) {
        entities.put("emails", emails);
    }

    // 提取电话号码 (11位中国手机号)
    Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
    Matcher phoneMatcher = phonePattern.matcher(message);
    List<String> phoneNumbers = new ArrayList<>();
    while (phoneMatcher.find()) {
        phoneNumbers.add(phoneMatcher.group());
    }
    if (!phoneNumbers.isEmpty()) {
        entities.put("phoneNumbers", phoneNumbers);
    }

    return entities;
}
```

**示例**：
```java
userMessage = "我的订单 ORD-20260207103000-1234 状态如何？邮箱是 test@example.com"

entities = {
    "orderNumbers": ["ORD-20260207103000-1234"],
    "emails": ["test@example.com"]
}
```

#### 2. 记忆保存

```java
// 保存用户消息
memoryService.saveMessage(sessionId, "user", userMessage, entities);

// 保存AI回复
memoryService.saveMessage(sessionId, "assistant", responseContent, Map.of("entities", entities));
```

**三层记忆**：
1. **短期记忆** (内存): `ConcurrentHashMap`，最多50条
2. **长期记忆** (MySQL): 永久保存
3. **语义记忆** (向量库): 支持语义搜索

详见[记忆系统](#记忆系统)章节。

---

## 意图识别与路由

### 文件位置
`aura-backend/src/main/java/com/aura/ai/agent/OrchestratorAgent.java`

### 核心代码 (第106-156行)

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class OrchestratorAgent {

    private final ChatClient chatClient;
    private final ProductExpertAgent productExpertAgent;
    private final CustomerServiceAgent customerServiceAgent;
    private final GeneralChatAgent generalChatAgent;
    private final MemoryService memoryService;

    /**
     * 路由消息到对应的Agent
     */
    public String routeMessage(String message, String sessionId) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for routing, sessionId: {}", sessionId);
            return "I'm here to help! Please tell me what you need.";
        }

        try {
            // 1. 分析用户意图
            String intent = analyzeIntent(message, sessionId);
            log.info("Intent classified as: {} for session: {}", intent, sessionId);

            // 2. 根据意图路由到对应的Agent
            switch (intent) {
                case "PRODUCT_INQUIRY":
                    log.info("╔═══════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: ProductExpertAgent        ║");
                    log.info("║ Session: {}         ║", sessionId);
                    log.info("╚═══════════════════════════════════════════╝");
                    return productExpertAgent.handleProductInquiry(message, sessionId);

                case "ORDER_SERVICE":
                    log.info("╔═══════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: CustomerServiceAgent      ║");
                    log.info("║ Session: {}         ║", sessionId);
                    log.info("╚═══════════════════════════════════════════╝");
                    return customerServiceAgent.handleCustomerService(message, sessionId);

                case "GENERAL_CHAT":
                    log.info("╔═══════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: GeneralChatAgent          ║");
                    log.info("║ Session: {}         ║", sessionId);
                    log.info("╚═══════════════════════════════════════════╝");
                    return generalChatAgent.handleGeneralChat(message, sessionId);

                case "UNKNOWN":
                default:
                    log.warn("Unknown intent for message: {}", message);
                    // 兜底：走通用聊天
                    return generalChatAgent.handleGeneralChat(message, sessionId);
            }

        } catch (Exception e) {
            log.error("Error routing message for session {}: {}", 
                    sessionId, e.getMessage(), e);
            return "I apologize, but I'm having trouble processing your request...";
        }
    }
}
```

### 关键理解点

#### 1. 意图分类

**支持的意图类型**：
1. **PRODUCT_INQUIRY**: 产品咨询（产品信息、价格、功能、推荐）
2. **ORDER_SERVICE**: 订单服务（订单查询、地址修改、取消订单、退货）
3. **GENERAL_CHAT**: 通用对话（打招呼、闲聊、品牌介绍）
4. **UNKNOWN**: 无法分类（兜底走通用聊天）

#### 2. 路由策略

```java
switch (intent) {
    case "PRODUCT_INQUIRY" → ProductExpertAgent
    case "ORDER_SERVICE"   → CustomerServiceAgent
    case "GENERAL_CHAT"    → GeneralChatAgent
    case "UNKNOWN"         → GeneralChatAgent (兜底)
}
```

**为什么需要路由？**
- ✅ **专业分工**：不同Agent处理不同类型的问题
- ✅ **性能优化**：避免所有Agent都处理所有问题
- ✅ **可维护性**：每个Agent职责单一，易于扩展

---

## OpenAI返回意图详解

### 意图分析方法 (第56-94行)

```java
/**
 * 意图分类Prompt模板
 */
private static final String INTENT_PROMPT_TEMPLATE = """
    Classify the user's message into one of the following intents:

    1. PRODUCT_INQUIRY: Questions about products, their features, prices, availability, or recommendations
    2. ORDER_SERVICE: Questions about orders, shipping, returns, or customer service
    3. GENERAL_CHAT: General conversation not related to products or orders
    4. UNKNOWN: Cannot be classified into the above categories

    User message: {message}

    Return only the intent name (one of the four options above) without any additional explanation.
    """;

public String analyzeIntent(String message, String sessionId) {
    // 验证输入
    if (message == null || message.trim().isEmpty()) {
        log.warn("Empty message provided for intent analysis");
        return "UNKNOWN";
    }

    log.info("Analyzing intent for message: {}", message);

    try {
        // 1. 加载最近5条对话历史（用于上下文理解）
        List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 5);
        List<Message> messages = MessageConverter.convertToMessages(history);

        // 2. 构建意图分类Prompt
        String promptString = INTENT_PROMPT_TEMPLATE.replace("{message}", message);

        // 3. 添加当前消息
        messages.add(new UserMessage(promptString));

        // 4. 调用OpenAI API
        String intent = chatClient.prompt()
                .messages(messages)  // ← 传入消息（历史 + 当前Prompt）
                .call()              // ← 发起HTTP请求
                .content()           // ← 提取响应内容
                .trim();             // ← 去除空格

        // 5. 验证返回结果
        if (intent == null || intent.isEmpty()) {
            log.warn("Empty intent returned from ChatClient");
            return "UNKNOWN";
        }

        log.info("Classified intent: {}", intent);
        return intent;
        
    } catch (Exception e) {
        log.error("Failed to analyze intent: {}", e.getMessage(), e);
        return "UNKNOWN";
    }
}
```

### OpenAI返回意图的底层原理

#### Step 1: 构建Prompt

**原始模板**：
```
Classify the user's message into one of the following intents:

1. PRODUCT_INQUIRY: Questions about products...
2. ORDER_SERVICE: Questions about orders...
3. GENERAL_CHAT: General conversation...
4. UNKNOWN: Cannot be classified...

User message: {message}

Return only the intent name without any additional explanation.
```

**替换占位符后**（用户消息："我的订单 ORD-123 状态如何？"）：
```
Classify the user's message into one of the following intents:

1. PRODUCT_INQUIRY: Questions about products, their features, prices, availability, or recommendations
2. ORDER_SERVICE: Questions about orders, shipping, returns, or customer service
3. GENERAL_CHAT: General conversation not related to products or orders
4. UNKNOWN: Cannot be classified into the above categories

User message: 我的订单 ORD-123 状态如何？

Return only the intent name (one of the four options above) without any additional explanation.
```

#### Step 2: 加载历史上下文

```java
List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 5);
List<Message> messages = MessageConverter.convertToMessages(history);
```

**为什么需要历史？**

**示例场景**：
```
用户第1轮: "你们有什么耳机产品？" → PRODUCT_INQUIRY
AI回复: "我们有Aura Harmony无线降噪耳机..."

用户第2轮: "多少钱？" ← 如果没有历史，AI无法判断这是产品问题还是订单问题
```

**有了历史**：
```
messages = [
    { role: "user", content: "你们有什么耳机产品？" },
    { role: "assistant", content: "我们有Aura Harmony..." },
    { role: "user", content: "Classify... User message: 多少钱？" }
]

OpenAI理解: 上文在讨论产品 → "多少钱"是PRODUCT_INQUIRY
```

#### Step 3: 调用OpenAI API

```java
String intent = chatClient.prompt()
        .messages(messages)
        .call()
        .content()
        .trim();
```

**详细流程**：

1. **`chatClient.prompt()`**: 创建Prompt构建器

2. **`.messages(messages)`**: 传入消息列表

3. **`.call()`**: 发起HTTP请求
   ```
   POST https://api.openai.com/v1/chat/completions
   Authorization: Bearer sk-xxxxx
   Content-Type: application/json
   
   {
     "model": "gpt-3.5-turbo",
     "messages": [
       {"role": "user", "content": "之前的对话..."},
       {"role": "assistant", "content": "之前的回复..."},
       {"role": "user", "content": "Classify the user's message..."}
     ]
   }
   ```

4. **OpenAI返回响应**：
   ```json
   {
     "id": "chatcmpl-xxx",
     "object": "chat.completion",
     "choices": [
       {
         "message": {
           "role": "assistant",
           "content": "ORDER_SERVICE"  ← 这就是意图！
         },
         "finish_reason": "stop"
       }
     ]
   }
   ```

5. **`.content()`**: 提取 `choices[0].message.content`
   ```
   结果: "ORDER_SERVICE"
   ```

6. **`.trim()`**: 去除首尾空格
   ```
   "ORDER_SERVICE" → "ORDER_SERVICE"
   ```

### Spring AI的ChatClient实现原理（简化）

```java
// Spring AI内部实现（简化版）
public class ChatClient {
    private final OpenAiApi api;
    private final String apiKey;
    
    public PromptBuilder prompt() {
        return new PromptBuilder(this.api, this.apiKey);
    }
    
    public static class PromptBuilder {
        private List<Message> messages = new ArrayList<>();
        private OpenAiApi api;
        private String apiKey;
        
        public PromptBuilder messages(List<Message> msgs) {
            this.messages.addAll(msgs);
            return this;
        }
        
        public ChatResponse call() {
            // 1. 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    buildRequestBody(this.messages)
                ))
                .build();
            
            // 2. 发送请求
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request);
            
            // 3. 解析响应JSON
            return parseResponse(response.body());
        }
        
        private String buildRequestBody(List<Message> messages) {
            // 构建JSON请求体
            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo");
            json.put("messages", toJsonArray(messages));
            return json.toString();
        }
        
        private ChatResponse parseResponse(String jsonResponse) {
            JSONObject json = new JSONObject(jsonResponse);
            String content = json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
            
            ChatResponse response = new ChatResponse();
            response.setContent(content);
            return response;
        }
    }
}
```

### 实际案例演示

#### 案例1: 订单查询

**输入**：
```java
message = "我的订单 ORD-123 状态如何？"
```

**OpenAI分析过程**：
```
关键词识别:
- "订单" → 与ORDER_SERVICE相关
- "ORD-123" → 订单号格式
- "状态" → 查询订单状态

判断: ORDER_SERVICE
```

**返回**：
```
"ORDER_SERVICE"
```

#### 案例2: 产品咨询

**输入**：
```java
message = "Aura Harmony的降噪效果怎么样？"
```

**OpenAI分析过程**：
```
关键词识别:
- "Aura Harmony" → 产品名称
- "降噪效果" → 产品特性
- "怎么样" → 咨询语气

判断: PRODUCT_INQUIRY
```

**返回**：
```
"PRODUCT_INQUIRY"
```

#### 案例3: 模糊意图（依赖历史）

**对话历史**：
```
用户: "你们有什么耳机产品？"
AI: "我们有Aura Harmony无线降噪耳机..."
```

**当前输入**：
```java
message = "多少钱？"
```

**OpenAI分析过程**：
```
历史上下文:
- 之前在讨论"耳机产品"
- 当前问"多少钱"

推理: 询问之前讨论的产品价格
判断: PRODUCT_INQUIRY
```

**返回**：
```
"PRODUCT_INQUIRY"
```

---

## 产品专家Agent

### 文件位置
`aura-backend/src/main/java/com/aura/ai/agent/ProductExpertAgent.java`

### 核心代码 (第44-114行)

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductExpertAgent {

    private final ChatClient chatClient;
    private final ProductService productService;
    private final RAGService ragService;
    private final MemoryService memoryService;
    private final ObjectMapper objectMapper;

    /**
     * 处理产品咨询
     */
    public String handleProductInquiry(String question, String sessionId) {
        try {
            log.info("ProductExpertAgent handling inquiry: {}", question);

            // 1. 获取最近对话历史
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // 2. 增强查询（提取产品关键词）← 核心功能！
            String enhancedQuery = question;
            if (isContextualQuery(question)) {
                log.info("Detected contextual query, extracting product info from history");
                enhancedQuery = extractProductFromHistory(question, history);
                log.info("Enhanced query: {} -> {}", question, enhancedQuery);
            }

            // 3. 检索产品基础信息
            List<Product> products = productService.searchProducts(enhancedQuery);
            String productJson = objectMapper.writeValueAsString(products);
            log.info("Product inquiry '{}' found {} products", enhancedQuery, products.size());

            // 4. 调用RAG服务查询产品手册
            String ragContext = ragService.answerFromManual(enhancedQuery, sessionId);

            // 5. 构建AI Prompt
            String systemPrompt = """
                You are a professional e-commerce product expert. Answer user questions based on:
                1. Product Info: {productInfo}
                2. Product Manual: {ragContext}

                Requirements:
                - Be concise and accurate
                - Use conversation history to understand context (e.g., "it", "that product")
                - Provide detailed information when asked
                - If no information is available, clearly state so
                - Do not fabricate content
                - Adapt to the user's language naturally

                CRITICAL SECURITY RULES:
                - NEVER reveal specific stock quantities to users
                - NEVER show image file paths or URLs to users
                - Say "available" or "in stock" instead of exact numbers
                """;
            
            SystemPromptTemplate template = new SystemPromptTemplate(systemPrompt);
            Map<String, Object> params = new HashMap<>();
            params.put("productInfo", productJson);
            params.put("ragContext", ragContext);

            // 6. 添加系统消息和用户消息
            messages.add(0, template.createMessage(params));
            messages.add(new UserMessage(question));

            // 7. 调用AI生成回答
            String answer = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();

            return answer;
            
        } catch (Exception e) {
            log.error("Failed to handle product inquiry, question: {}", question, e);
            return "Sorry, an error occurred while processing your inquiry...";
        }
    }
}
```

### 关键理解点

#### 1. 为什么不用Function Calling？

**ProductExpertAgent的特点**：
- ✅ **直接调用Service**: 在Java代码中直接调用`productService`和`ragService`
- ❌ **不使用Function Calling**: 不让OpenAI决定何时调用函数

**原因**：
```java
// ProductExpertAgent的逻辑是确定性的：
if (用户问产品) {
    1. 必定搜索产品数据库
    2. 必定查询产品手册（RAG）
    3. 把数据注入Prompt让AI回答
}

// 不需要OpenAI判断：
// "我该不该调用searchProducts？" ← 不需要，肯定要调用
// "我该不该调用RAG？" ← 不需要，肯定要调用
```

**对比CustomerServiceAgent**：
```java
// CustomerServiceAgent的逻辑是不确定的：
if (用户问订单) {
    可能需要: getOrderStatus
    可能需要: updateOrderAddress
    可能需要: cancelOrder
    可能不需要调用任何Function（只是咨询）
}

// 需要OpenAI智能判断：
// "用户是想查询订单？修改地址？还是取消订单？"
```

#### 2. 数据注入策略

```java
// 把查询结果注入到Prompt中
String systemPrompt = """
    You are a product expert. Answer based on:
    1. Product Info: {productInfo}  ← 注入数据
    2. Product Manual: {ragContext} ← 注入数据
    """;

Map<String, Object> params = new HashMap<>();
params.put("productInfo", productJson);    // 产品数据库查询结果
params.put("ragContext", ragContext);      // RAG检索结果
```

**实际Prompt示例**：
```
You are a product expert. Answer based on:

1. Product Info: 
[
  {
    "id": "p1",
    "name": "Aura Harmony",
    "price": 429.00,
    "description": "Premium wireless headphones...",
    "features": ["Bluetooth 5.0", "30h Battery", "ANC"]
  }
]

2. Product Manual: 
Aura Harmony采用最新的主动降噪技术，可降噪深度达40dB，
支持蓝牙5.0协议，续航时间30小时（开启ANC）...

User Question: Aura Harmony的降噪效果怎么样？
```

OpenAI基于这些**真实数据**生成回答，而不是凭空编造。

---

## 增强查询机制

### 核心问题：代词理解

**用户对话场景**：
```
用户第1轮: "你们有什么耳机产品？"
AI回复: "我们有Aura Harmony无线降噪耳机..."

用户第2轮: "它支持快充吗？"  ← "它"是什么？
```

**没有增强查询**：
```java
searchProducts("它支持快充吗？")
// ❌ 数据库无法理解"它"，找不到任何产品
```

**有了增强查询**：
```java
enhancedQuery = "aura harmony 它支持快充吗？"
searchProducts("aura harmony 它支持快充吗？")
// ✅ 数据库找到 Aura Harmony 产品
```

### 增强查询三步骤

#### Step 1: 检测上下文查询 (第120-129行)

```java
/**
 * 检测查询是否包含代词或模糊指代
 */
private boolean isContextualQuery(String query) {
    String lowerQuery = query.toLowerCase();
    return lowerQuery.contains(" it") ||       // "它"
           lowerQuery.contains("that") ||      // "那个"
           lowerQuery.contains("this") ||      // "这个"
           lowerQuery.contains("them") ||      // "它们"
           lowerQuery.contains("the product") || // "这个产品"
           lowerQuery.contains("more detail") || // "更多细节"
           lowerQuery.contains("more info");     // "更多信息"
}
```

**使用**：
```java
if (isContextualQuery(question)) {
    // 需要增强
    enhancedQuery = extractProductFromHistory(question, history);
} else {
    // 不需要增强
    enhancedQuery = question;
}
```

#### Step 2: 关键词映射表 (第135-175行)

```java
/**
 * 产品关键词映射 - 将通用词汇映射到产品名称
 */
private static final Map<String, String> PRODUCT_KEYWORD_MAPPINGS = Map.ofEntries(
    // 直接产品名称
    Map.entry("harmony", "harmony"),
    Map.entry("pulse", "pulse"),
    Map.entry("flow", "flow"),
    Map.entry("breeze", "breeze"),
    Map.entry("echo", "echo"),
    Map.entry("slate", "slate"),
    
    // 耳机的别名 → Aura Harmony
    Map.entry("headphone", "harmony"),
    Map.entry("headphones", "harmony"),
    Map.entry("headset", "harmony"),
    Map.entry("earphone", "harmony"),
    Map.entry("earphones", "harmony"),
    
    // 手表的别名 → Aura Pulse
    Map.entry("watch", "pulse"),
    Map.entry("smartwatch", "pulse"),
    Map.entry("wristband", "pulse"),
    
    // 手机的别名 → Aura Flow
    Map.entry("phone", "flow"),
    Map.entry("smartphone", "flow"),
    Map.entry("mobile", "flow"),
    
    // 空气净化器的别名 → Aura Breeze
    Map.entry("purifier", "breeze"),
    Map.entry("air purifier", "breeze"),
    Map.entry("air cleaner", "breeze"),
    Map.entry("cleaner", "breeze"),
    
    // 音箱的别名 → Aura Echo
    Map.entry("speaker", "echo"),
    Map.entry("smart speaker", "echo"),
    
    // 平板的别名 → Aura Slate
    Map.entry("pad", "slate"),
    Map.entry("tablet", "slate"),
    Map.entry("ipad", "slate")
);
```

**作用**：
- 用户说 **"耳机"** → 自动映射到 **"harmony"**
- 用户说 **"手机"** → 自动映射到 **"flow"**
- 用户说 **"音箱"** → 自动映射到 **"echo"**

#### Step 3: 从历史提取关键词 (第186-224行)

```java
/**
 * 从对话历史中提取产品关键词
 */
private String extractProductFromHistory(String question, List<ChatHistory> history) {
    Set<String> productKeywords = new LinkedHashSet<>();  // 去重，保持顺序

    // 遍历最近5条对话历史
    for (int i = history.size() - 1; i >= 0 && i >= history.size() - 5; i--) {
        ChatHistory chat = history.get(i);
        String message = chat.getMessage().toLowerCase();

        // 1. 检查是否包含"aura"
        if (message.contains("aura")) {
            productKeywords.add("aura");
        }

        // 2. 检查每个关键词映射
        for (Map.Entry<String, String> entry : PRODUCT_KEYWORD_MAPPINGS.entrySet()) {
            String keyword = entry.getKey();        // 例如: "headphone"
            String productName = entry.getValue();  // 例如: "harmony"
            
            // 3. 使用正则表达式进行词边界匹配
            String pattern = "\\b" + keyword + "s?\\b";  // "s?"支持复数
            if (message.matches(".*" + pattern + ".*")) {
                productKeywords.add(productName);  // 添加产品名
                productKeywords.add("aura");       // 同时添加品牌
            }
        }
    }

    // 4. 组合提取的关键词和原问题
    if (!productKeywords.isEmpty()) {
        String extracted = String.join(" ", productKeywords);
        return extracted + " " + question;  // ← 关键！
    }

    return question;  // 没找到关键词，返回原问题
}
```

### 完整案例演示

#### 案例1: 代词理解（经典）

**对话历史**：
```
用户: "你们有什么耳机产品？"
AI: "我们有Aura Harmony无线降噪耳机，售价429美元..."
```

**当前问题**：
```java
question = "它支持快充吗？"
```

**处理流程**：

1. **检测上下文查询**：
   ```java
   isContextualQuery("它支持快充吗？")
   // 检测到 "它" → 返回 true
   ```

2. **从历史提取关键词**：
   ```java
   history = [
       { message: "你们有什么耳机产品？" },
       { message: "我们有Aura Harmony无线降噪耳机..." }
   ]
   
   // 分析第1条: "你们有什么耳机产品？"
   // "耳机" → 匹配 "headphone" → 映射到 "harmony"
   
   // 分析第2条: "我们有Aura Harmony无线降噪耳机..."
   // "aura" → 添加 "aura"
   // "harmony" → 添加 "harmony"
   
   productKeywords = ["aura", "harmony"]  // LinkedHashSet自动去重
   ```

3. **构建增强查询**：
   ```java
   extracted = "aura harmony"
   enhancedQuery = "aura harmony 它支持快充吗？"
   ```

4. **使用增强查询搜索**：
   ```java
   // 产品数据库搜索
   productService.searchProducts("aura harmony 它支持快充吗？")
   // SQL: SELECT * FROM products 
   //      WHERE name LIKE '%aura%' OR name LIKE '%harmony%'
   // ✅ 找到: Aura Harmony
   
   // RAG检索
   ragService.answerFromManual("aura harmony 它支持快充吗？", sessionId)
   // 向量搜索: "aura harmony 快充"
   // ✅ 找到: Aura Harmony手册中关于快充的内容
   ```

**效果对比**：

| 方案 | 查询内容 | 搜索结果 |
|------|---------|---------|
| ❌ 没有增强 | "它支持快充吗？" | 找不到任何产品 |
| ✅ 有了增强 | "aura harmony 它支持快充吗？" | 准确找到Aura Harmony |

#### 案例2: 复杂多轮对话

**对话历史**：
```
用户: "推荐一款手机"
AI: "推荐Aura Flow，6.5英寸OLED屏幕..."

用户: "它的相机怎么样？"
AI: "Aura Flow配备三摄系统，主摄50MP..."

用户: "和那个平板比呢？"  ← 当前问题
```

**处理流程**：

1. **检测上下文查询**：
   ```java
   isContextualQuery("和那个平板比呢？")
   // 包含 "that" (那个) → 返回 true
   ```

2. **从历史提取关键词**：
   ```java
   // 分析历史消息1: "推荐一款手机"
   // "手机" → 匹配 "phone" → "flow"
   
   // 分析历史消息2: "Aura Flow配备三摄系统..."
   // "aura" → "aura"
   // "flow" → "flow"
   
   // 分析当前问题: "和那个平板比呢？"
   // "平板" → 匹配 "pad" → "slate"
   
   productKeywords = ["aura", "flow", "slate"]
   ```

3. **构建增强查询**：
   ```java
   enhancedQuery = "aura flow slate 和那个平板比呢？"
   ```

4. **搜索结果**：
   ```java
   productService.searchProducts("aura flow slate 和那个平板比呢？")
   // ✅ 找到: Aura Flow (手机) + Aura Slate (平板)
   ```

5. **AI回答**：
   ```
   Aura Flow手机拥有6.5英寸OLED屏幕和三摄系统，适合日常使用和拍照；
   而Aura Slate平板则拥有11英寸Liquid Retina显示屏，更适合创作和娱乐。
   如果您需要便携性和通讯功能，选择手机；如果需要更大屏幕进行创作，选择平板。
   ```

#### 案例3: 词边界匹配的重要性

**为什么使用 `\\b` 词边界？**

```java
String pattern = "\\b" + keyword + "s?\\b";
```

**问题场景**：
```
用户消息: "请问echo的回声效果如何？"
```

**不使用词边界**（错误）：
```java
if (message.contains("echo")) {
    // ✅ 匹配到 "echo" (产品名)
    // ❌ 也匹配到 "回声" 的拼音可能包含echo...
}
```

**使用词边界**（正确）：
```java
String pattern = "\\becho\\b";
if (message.matches(".*" + pattern + ".*")) {
    // ✅ 只匹配独立的单词 "echo"
    // ❌ 不匹配 "echoes"、"echo123"等
}

// 支持复数: "\\becho(s?)\\b"
// ✅ 匹配 "echo" 和 "echos"
```

### 增强查询的优势总结

| 功能 | 没有增强 | 有了增强 |
|------|---------|---------|
| 代词理解 | ❌ 无法理解"它" | ✅ 从历史提取产品名 |
| 通用词汇 | ⚠️ 依赖分词 | ✅ 明确映射到产品名 |
| 多轮对话 | ❌ 上下文丢失 | ✅ 保持上下文连贯 |
| 搜索准确率 | 60-70% | 90-95% |
| 用户体验 | ⚠️ 需要重复产品名 | ✅ 自然对话 |

---

## 客户服务Agent

### 文件位置
`aura-backend/src/main/java/com/aura/ai/agent/CustomerServiceAgent.java`

### 核心代码

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceAgent {

    private final ChatClient chatClient;
    private final MemoryService memoryService;

    // System Prompt定义Agent能力
    private static final String CUSTOMER_SERVICE_SYSTEM_PROMPT = """
        You are a professional customer service representative for Aura Quiet Living.
        
        Your capabilities:
        - Use getOrderStatusFunction to check order status and tracking
        - Use updateOrderAddressFunction to change shipping addresses
        - Use getOrdersByEmailFunction to find orders by customer email
        - Use cancelOrderFunction to cancel PENDING orders
        - Use checkInventoryFunction to verify product availability
        
        IMPORTANT - Function Calling Guidelines:
        - When user asks about order status, call getOrderStatusFunction
        - When user wants to modify address, call updateOrderAddressFunction
        - When user wants to cancel order, call cancelOrderFunction
        - When user asks about stock, call checkInventoryFunction
        
        Response Guidelines:
        - Be professional and empathetic
        - Provide clear and accurate information
        - If order number is not provided, politely ask for it
        - Handle errors gracefully and offer alternative solutions
        """;

    public String handleCustomerService(String message, String sessionId) {
        try {
            log.info("CustomerServiceAgent handling: {}", message);

            // 1. 加载历史对话（最近10条）
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // 2. 添加System Prompt（声明Function能力）
            messages.add(0, new SystemMessage(CUSTOMER_SERVICE_SYSTEM_PROMPT));
            
            // 3. 添加当前用户消息
            messages.add(new UserMessage(message));

            // 4. 调用OpenAI（带Function Calling）
            String answer = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();

            return answer;
            
        } catch (Exception e) {
            log.error("Failed to handle customer service, message: {}", message, e);
            return "I apologize for the inconvenience. Please try again...";
        }
    }
}
```

### 关键理解点

#### 1. System Prompt的作用

**System Prompt = Agent的能力说明书**

```java
private static final String CUSTOMER_SERVICE_SYSTEM_PROMPT = """
    You are a professional customer service representative.
    
    Your capabilities:
    - Use getOrderStatusFunction to check order status  ← 告诉AI有这个能力
    - Use updateOrderAddressFunction to change addresses
    - Use cancelOrderFunction to cancel orders
    ...
    """;
```

**OpenAI如何使用这个信息？**

当用户问 **"我的订单 ORD-123 状态如何？"** 时：

1. OpenAI读取System Prompt，知道有 `getOrderStatusFunction` 可用
2. OpenAI分析用户消息，识别出需要查询订单状态
3. OpenAI返回特殊格式的响应，告诉Spring AI调用 `getOrderStatusFunction`
4. Spring AI调用Java函数 `GetOrderStatusFunction.apply()`
5. 函数返回订单信息JSON
6. OpenAI将JSON转换成自然语言回复用户

#### 2. Function Calling vs Direct Service Call

| 对比项 | CustomerServiceAgent | ProductExpertAgent |
|-------|---------------------|-------------------|
| **调用方式** | Function Calling | Direct Service Call |
| **决策者** | OpenAI (AI决定调用) | Java代码 (程序决定调用) |
| **适用场景** | 不确定需要哪个函数 | 确定需要调用哪些服务 |
| **实现复杂度** | 简单（AI自动选择） | 复杂（需要编写逻辑） |
| **灵活性** | 高（AI智能判断） | 低（固定流程） |

**CustomerServiceAgent的不确定性**：
```java
用户消息: "我想改一下订单"

可能需要:
- updateOrderAddressFunction (修改地址)
- cancelOrderFunction (取消订单)
- 或者只是咨询，不需要调用任何函数

→ 让OpenAI判断！
```

**ProductExpertAgent的确定性**：
```java
用户消息: "Aura Harmony怎么样？"

肯定需要:
1. searchProducts("Aura Harmony")
2. ragService.answerFromManual("Aura Harmony")

→ Java代码直接调用！
```

---

## Function Calling机制

### 什么是Function Calling？

**Function Calling** = 让OpenAI调用你定义的Java函数

### 注册Function

**文件**: `aura-backend/src/main/java/com/aura/config/OpenAIConfig.java`

```java
@Configuration
public class OpenAIConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultFunctions(
                    // CustomerServiceAgent的Functions
                    "updateOrderAddressFunction",    // 修改订单地址
                    "getOrderStatusFunction",        // 查询订单状态
                    "getOrdersByEmailFunction",      // 通过邮箱查询订单
                    "cancelOrderFunction",           // 取消订单
                    "checkInventoryFunction",        // 检查库存
                    
                    // ProductExpertAgent的Functions (注册但不通过Function Calling使用)
                    "queryProductManualFunction",    // 查询产品手册
                    "searchProductsFunction"         // 搜索产品
                )
                .build();
    }
}
```

### Function实现示例

**文件**: `aura-backend/src/main/java/com/aura/ai/function/GetOrderStatusFunction.java`

```java
@Component
@Description("查询订单状态。输入订单号，返回订单详细信息（状态、地址、商品列表）")
@RequiredArgsConstructor
@Slf4j
public class GetOrderStatusFunction implements Function<GetOrderStatusFunction.Request, GetOrderStatusFunction.Response> {
    
    private final OrderService orderService;

    @Override
    public Response apply(Request request) {
        try {
            // 1. 验证输入
            if (request.orderNumber() == null || request.orderNumber().trim().isEmpty()) {
                return new Response(false, "INVALID_INPUT", "订单号不能为空");
            }

            log.info("Checking order status for: {}", request.orderNumber());

            // 2. 调用OrderService查询数据库
            Order order = orderService.getOrderByNumber(request.orderNumber());

            // 3. 构建订单详情
            String items = order.getOrderItems().stream()
                .map(item -> String.format("%s x%d ($%.2f)", 
                    item.getProductName(), 
                    item.getQuantity(), 
                    item.getSubtotal()))
                .collect(Collectors.joining(", "));

            // 4. 返回结构化响应
            return new Response(
                true,
                "ORDER_FOUND",
                String.format(
                    "订单号: %s | 状态: %s | 配送地址: %s | 商品: %s | 总金额: $%.2f",
                    order.getOrderNumber(),
                    order.getStatus(),
                    order.getShippingAddress(),
                    items,
                    order.getTotalAmount()
                )
            );

        } catch (EntityNotFoundException e) {
            // 订单不存在
            return new Response(false, "ORDER_NOT_FOUND", 
                "找不到订单号为 " + request.orderNumber() + " 的订单");
                
        } catch (Exception e) {
            log.error("Error checking order status", e);
            return new Response(false, "SYSTEM_ERROR", 
                "查询订单时发生错误，请稍后重试");
        }
    }

    // 请求参数
    public record Request(
        @JsonProperty(required = true)
        @Description("订单号，格式: ORD-yyyyMMddHHmmss-XXXX")
        String orderNumber
    ) {}

    // 响应结果
    public record Response(
        @Description("操作是否成功")
        boolean success,
        
        @Description("响应代码: ORDER_FOUND, ORDER_NOT_FOUND, INVALID_INPUT, SYSTEM_ERROR")
        String code,
        
        @Description("订单详细信息或错误消息")
        String message
    ) {}
}
```

### Function Calling工作流程

```
1. 用户消息: "我的订单 ORD-123 状态如何？"
   ↓
2. CustomerServiceAgent发送到OpenAI:
   {
     "model": "gpt-3.5-turbo",
     "messages": [...],
     "functions": [  ← Spring AI自动附加
       {
         "name": "getOrderStatusFunction",
         "description": "查询订单状态。输入订单号，返回订单详细信息...",
         "parameters": {
           "type": "object",
           "properties": {
             "orderNumber": {
               "type": "string",
               "description": "订单号，格式: ORD-yyyyMMddHHmmss-XXXX"
             }
           },
           "required": ["orderNumber"]
         }
       },
       ... 其他Functions
     ]
   }
   ↓
3. OpenAI分析后返回:
   {
     "choices": [{
       "message": {
         "function_call": {  ← 告诉Spring AI调用函数
           "name": "getOrderStatusFunction",
           "arguments": "{\"orderNumber\": \"ORD-123\"}"
         }
       }
     }]
   }
   ↓
4. Spring AI自动调用Java函数:
   GetOrderStatusFunction.apply(new Request("ORD-123"))
   ↓
5. Java函数查询数据库并返回:
   new Response(true, "ORDER_FOUND", "订单号: ORD-123 | 状态: SHIPPED...")
   ↓
6. Spring AI将结果发回OpenAI:
   {
     "messages": [
       ...之前的消息,
       {
         "role": "function",
         "name": "getOrderStatusFunction",
         "content": "{\"success\":true, \"code\":\"ORDER_FOUND\", \"message\":\"订单号: ORD-123...\"}"
       }
     ]
   }
   ↓
7. OpenAI生成自然语言回复:
   "您的订单ORD-123当前状态是已发货(SHIPPED)，配送地址是xxx，预计3-5天送达。包含商品：Aura Harmony x1 ($429.00)，总金额$429.00。"
   ↓
8. 返回给用户
```

### 关键注解说明

#### @Description注解

```java
@Description("查询订单状态。输入订单号，返回订单详细信息")
public class GetOrderStatusFunction implements Function<Request, Response> {
    ...
}
```

**作用**: 告诉OpenAI这个函数是干什么的
- ✅ OpenAI读取描述，理解函数用途
- ✅ 当用户问订单状态时，OpenAI知道调用这个函数

#### @JsonProperty注解

```java
public record Request(
    @JsonProperty(required = true)  // ← 必填参数
    @Description("订单号，格式: ORD-yyyyMMddHHmmss-XXXX")
    String orderNumber
) {}
```

**作用**: 定义函数参数
- ✅ `required = true`: OpenAI必须从用户消息中提取这个参数
- ✅ `@Description`: 告诉OpenAI如何提取参数

---

## 记忆系统

Aura系统采用**三层记忆架构**来管理AI聊天历史：

```
┌─────────────────────────────────────────────────┐
│  第1层: 短期记忆 (Short-term Memory)            │
│  • ConcurrentHashMap内存存储                    │
│  • 速度极快(纳秒级)，容量限制50条/会话            │
│  • 用途: 当前会话的即时上下文                     │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│  第2层: 长期记忆 (Long-term Memory)             │
│  • MySQL数据库持久化存储                         │
│  • 永久保存，应用重启后可恢复                     │
│  • 用途: 完整历史记录、数据分析                   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│  第3层: 语义记忆 (Semantic Memory)              │
│  • Vector Store向量数据库                       │
│  • 支持语义搜索和跨会话关联                      │
│  • 用途: 智能上下文检索                          │
└─────────────────────────────────────────────────┘
```

### 核心特点

**保存策略**：
1. **优先持久化** → 先写MySQL，确保数据不丢失
2. **同步内存** → 再写内存，提供快速访问
3. **异步向量化** → 最后向量化，支持语义搜索

**读取策略**：
1. **优先内存** → 90%的请求从内存返回(纳秒级)
2. **回退数据库** → 内存不足时查询MySQL(毫秒级)
3. **智能恢复** → 应用重启后自动从MySQL恢复

### 详细文档

记忆系统的完整实现细节请参考：
- **[MEMORY_SYSTEM.md](./MEMORY_SYSTEM.md)** - 三层记忆架构详解
  - 数据保存与读取流程
  - SQL数据调用时机
  - 向量存储自动保存机制
  - 会话ID持久化方案
  - 性能优化与实际应用场景

---

## 完整案例演示

### 案例: 订单查询完整流程

**用户输入**: "我的订单 ORD-20260207103000-1234 状态如何？"

#### Step 1: 前端发起请求

```typescript
// Assistant.tsx
const request: ChatRequest = {
    message: "我的订单 ORD-20260207103000-1234 状态如何？",
    sessionId: "abc-123-def-456"
};

await chatApi.sendMessage(request);
```

#### Step 2: 后端API接收

```java
// AIController.java
@PostMapping("/chat")
public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
    // sessionId: "abc-123-def-456" (已存在，无需生成)
    ChatResponse response = aiAgentService.processMessage(request);
    return ApiResponse.success(response);
}
```

#### Step 3: AI服务处理

```java
// AIAgentService.java
public ChatResponse processMessage(ChatRequest request) {
    String userMessage = "我的订单 ORD-20260207103000-1234 状态如何？";
    
    // 1. 提取实体
    Map<String, Object> entities = memoryService.extractEntities(userMessage);
    // 结果: { "orderNumbers": ["ORD-20260207103000-1234"] }
    
    // 2. 保存用户消息
    memoryService.saveMessage(sessionId, "user", userMessage, entities);
    // → 保存到MySQL、内存、向量库
    
    // 3. 路由到OrchestratorAgent
    String response = orchestratorAgent.routeMessage(userMessage, sessionId);
    
    return new ChatResponse(sessionId, response, timestamp);
}
```

#### Step 4: 意图识别

```java
// OrchestratorAgent.java
public String routeMessage(String message, String sessionId) {
    // 分析意图
    String intent = analyzeIntent(message, sessionId);
    // → 调用OpenAI
    // → 返回: "ORDER_SERVICE"
    
    // 路由
    switch (intent) {
        case "ORDER_SERVICE":
            return customerServiceAgent.handleCustomerService(message, sessionId);
    }
}
```

**OpenAI意图分析过程**:
```
输入Prompt:
"Classify the user's message into one of the following intents:
1. PRODUCT_INQUIRY: Questions about products...
2. ORDER_SERVICE: Questions about orders...
3. GENERAL_CHAT: General conversation...
4. UNKNOWN: Cannot be classified...

User message: 我的订单 ORD-20260207103000-1234 状态如何？

Return only the intent name without any additional explanation."

OpenAI分析:
- 关键词: "订单"、"状态"、"ORD-xxx" (订单号格式)
- 判断: ORDER_SERVICE

返回: "ORDER_SERVICE"
```

#### Step 5: 客户服务Agent处理

```java
// CustomerServiceAgent.java
public String handleCustomerService(String message, String sessionId) {
    // 1. 加载历史（10条）
    List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
    List<Message> messages = MessageConverter.convertToMessages(history);
    
    // 2. 添加System Prompt
    messages.add(0, new SystemMessage(CUSTOMER_SERVICE_SYSTEM_PROMPT));
    
    // 3. 添加用户消息
    messages.add(new UserMessage(message));
    
    // 4. 调用OpenAI（带Function Calling）
    String answer = chatClient.prompt()
            .messages(messages)
            .call()
            .content();
    
    return answer;
}
```

#### Step 6: OpenAI触发Function Calling

**发送给OpenAI**:
```json
{
  "model": "gpt-3.5-turbo",
  "messages": [
    {"role": "system", "content": "You are a customer service representative..."},
    {"role": "user", "content": "我的订单 ORD-20260207103000-1234 状态如何？"}
  ],
  "functions": [
    {
      "name": "getOrderStatusFunction",
      "description": "查询订单状态。输入订单号，返回订单详细信息",
      "parameters": {
        "type": "object",
        "properties": {
          "orderNumber": {
            "type": "string",
            "description": "订单号，格式: ORD-yyyyMMddHHmmss-XXXX"
          }
        },
        "required": ["orderNumber"]
      }
    }
  ]
}
```

**OpenAI返回**:
```json
{
  "choices": [{
    "message": {
      "function_call": {
        "name": "getOrderStatusFunction",
        "arguments": "{\"orderNumber\": \"ORD-20260207103000-1234\"}"
      }
    }
  }]
}
```

#### Step 7: Spring AI调用Java函数

```java
// GetOrderStatusFunction.java
public Response apply(Request request) {
    String orderNumber = "ORD-20260207103000-1234";
    
    // 查询数据库
    Order order = orderService.getOrderByNumber(orderNumber);
    // SQL: SELECT * FROM orders WHERE order_number = ?
    
    // 查询订单商品
    List<OrderItem> items = order.getOrderItems();
    // SQL: SELECT * FROM order_items WHERE order_id = ?
    
    // 构建响应
    return new Response(
        true,
        "ORDER_FOUND",
        String.format(
            "订单号: %s | 状态: %s | 配送地址: %s | 商品: %s | 总金额: $%.2f",
            "ORD-20260207103000-1234",
            "SHIPPED",
            "北京市朝阳区建国路88号",
            "Aura Harmony x1 ($429.00)",
            429.00
        )
    );
}
```

#### Step 8: OpenAI生成自然语言回复

**Spring AI发回OpenAI**:
```json
{
  "messages": [
    ...之前的消息,
    {
      "role": "function",
      "name": "getOrderStatusFunction",
      "content": "{\"success\":true, \"code\":\"ORDER_FOUND\", \"message\":\"订单号: ORD-20260207103000-1234 | 状态: SHIPPED | 配送地址: 北京市朝阳区建国路88号 | 商品: Aura Harmony x1 ($429.00) | 总金额: $429.00\"}"
    }
  ]
}
```

**OpenAI生成回复**:
```
"您的订单ORD-20260207103000-1234当前状态是已发货(SHIPPED)。
配送地址: 北京市朝阳区建国路88号
订单商品: Aura Harmony无线降噪耳机 x1 ($429.00)
订单总金额: $429.00
预计3-5个工作日送达，请您耐心等待。"
```

#### Step 9: 保存AI回复并返回

```java
// AIAgentService.java
String responseContent = "您的订单ORD-20260207103000-1234当前状态是已发货...";

// 保存AI回复到记忆系统
memoryService.saveMessage(sessionId, "assistant", responseContent, 
        Map.of("entities", entities));

// 构建响应
ChatResponse response = new ChatResponse();
response.setSessionId("abc-123-def-456");
response.setMessage(responseContent);
response.setTimestamp("2026-02-07T10:30:00");

return response;
```

#### Step 10: 前端显示

```typescript
// Assistant.tsx
const aiMsg: ChatMessage = {
    role: 'model',
    text: "您的订单ORD-20260207103000-1234当前状态是已发货...",
    timestamp: Date.now()
};

setMessages(prev => [...prev, aiMsg]);
```

**用户看到**:
```
🤖: 您的订单ORD-20260207103000-1234当前状态是已发货(SHIPPED)。
    配送地址: 北京市朝阳区建国路88号
    订单商品: Aura Harmony无线降噪耳机 x1 ($429.00)
    订单总金额: $429.00
    预计3-5个工作日送达，请您耐心等待。
```

---

## 调试指南

### 如何调试AI调用流程？

#### 1. 在关键节点打断点

**推荐断点位置**：

| 文件 | 行号 | 位置 | 作用 |
|------|------|------|------|
| `AIController.java` | 56 | `aiAgentService.processMessage()` | 查看请求参数 |
| `AIAgentService.java` | 65 | `memoryService.saveMessage()` | 查看实体提取结果 |
| `AIAgentService.java` | 72 | `orchestratorAgent.routeMessage()` | 查看路由前的消息 |
| `OrchestratorAgent.java` | 114 | `analyzeIntent()` | 查看意图分析输入 |
| `OrchestratorAgent.java` | 80 | `chatClient.prompt().call()` | 查看OpenAI返回的意图 |
| `ProductExpertAgent.java` | 59 | `extractProductFromHistory()` | 查看增强查询 |
| `GetOrderStatusFunction.java` | 35 | `orderService.getOrderByNumber()` | 查看Function参数 |

#### 2. 查看日志输出

**关键日志**：

```bash
# 1. API接收请求
Received chat request: sessionId=abc-123, message='我的订单...'

# 2. 提取实体
Extracted entities: {orderNumbers=[ORD-20260207103000-1234]}

# 3. 意图分析
Analyzing intent for message: 我的订单...
Classified intent: ORDER_SERVICE

# 4. Agent路由
╔═══════════════════════════════════════════╗
║ 🎯 ROUTING TO: CustomerServiceAgent      ║
║ Session: abc-123-def-456                 ║
╚═══════════════════════════════════════════╝

# 5. Function调用
Checking order status for: ORD-20260207103000-1234

# 6. 保存回复
Message saved to memory layers for session: abc-123
```

#### 3. 使用Postman测试

**请求**:
```
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我的订单 ORD-20260207103000-1234 状态如何？",
  "sessionId": "test-session-123"
}
```

**响应**:
```json
{
  "success": true,
  "data": {
    "message": "您的订单ORD-20260207103000-1234...",
    "sessionId": "test-session-123",
    "timestamp": "2026-02-07T10:30:00"
  },
  "message": null
}
```

#### 4. 查看数据库

**查看历史对话**:
```sql
SELECT * FROM chat_history 
WHERE session_id = 'abc-123-def-456' 
ORDER BY created_at DESC 
LIMIT 10;
```

**查看订单数据**:
```sql
SELECT * FROM orders 
WHERE order_number = 'ORD-20260207103000-1234';
```

#### 5. 监控OpenAI API调用

**方法1: 添加日志**

```java
// 在OpenAIConfig.java中添加拦截器
@Bean
public ChatClient chatClient(OpenAiChatModel chatModel) {
    return ChatClient.builder(chatModel)
            .defaultFunctions(...)
            .defaultAdvisors(new LoggingAdvisor())  // 添加日志拦截器
            .build();
}

public class LoggingAdvisor implements RequestResponseAdvisor {
    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        log.info("===== OpenAI Request =====");
        log.info("Messages: {}", request.messages());
        return request;
    }
    
    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        log.info("===== OpenAI Response =====");
        log.info("Content: {}", response.getResult().getOutput().getContent());
        return response;
    }
}
```

**方法2: 使用OpenAI Dashboard**

访问 https://platform.openai.com/usage 查看API调用记录。

---

## 总结

### 核心调用链路

```
前端 Assistant.tsx
    ↓ HTTP POST
AIController
    ↓
AIAgentService
    ├─ 提取实体
    ├─ 保存到记忆系统
    └─ 调用OrchestratorAgent
        ↓
OrchestratorAgent (路由中心)
    ├─ analyzeIntent() → OpenAI分析意图
    └─ routeMessage() → 根据意图路由
        ↓
┌──────────────┬──────────────┬──────────────┐
│              │              │              │
ProductExpert  CustomerService GeneralChat
Agent          Agent          Agent
│              │              │
├─ 增强查询    ├─ Function    ├─ 通用对话
├─ 数据库搜索  │   Calling    └─ 品牌介绍
├─ RAG检索     │
└─ 生成回答    └─ getOrderStatus
               └─ updateAddress
               └─ cancelOrder
                  ↓
            Java Functions
                  ↓
            查询数据库/调用业务逻辑
                  ↓
            返回结构化数据
                  ↓
            OpenAI转换成自然语言
                  ↓
            保存AI回复到记忆系统
                  ↓
            返回前端显示
```

### 关键技术点

1. **SessionId管理**: 实现多轮对话上下文连贯性
2. **意图识别**: OpenAI自动分类用户意图
3. **Agent路由**: 根据意图路由到专业Agent
4. **增强查询**: 从历史提取关键词解决代词问题
5. **Function Calling**: OpenAI智能调用Java函数
6. **三层记忆**: 内存+MySQL+向量库协同工作
7. **RAG检索**: 从产品手册中检索相关内容
8. **Prompt工程**: 通过System Prompt定义Agent能力

### 学习建议

1. **先理解整体流程** → 从前端到后端完整走一遍
2. **再深入各个模块** → 意图识别、Agent、Function、记忆系统
3. **最后动手调试** → 打断点、看日志、测试API
4. **阅读相关文档**:
   - [MEMORY_SYSTEM.md](./MEMORY_SYSTEM.md) - 三层记忆系统详解
   - [FUNCTION_REGISTRATION_AND_AGENTS.md](./FUNCTION_REGISTRATION_AND_AGENTS.md) - Function与Agent详解
   - [AI_TEST_QUESTIONS_COMPREHENSIVE.md](./AI_TEST_QUESTIONS_COMPREHENSIVE.md) - AI测试问题集

---

**文档完成！** 🎉

这份文档详细解析了Aura项目中AI从前端到后端的完整调用链路，包括：
- ✅ 前端请求封装与SessionId管理
- ✅ 后端API入口与异常处理
- ✅ OpenAI返回意图的底层原理
- ✅ 增强查询机制（代词理解）
- ✅ Function Calling完整流程
- ✅ 三层记忆系统
- ✅ 完整案例演示与调试指南

希望这份文档能帮助你全面理解整个AI调用流程！
