# AI聊天系统完整调用流程详解

## 📊 整体架构图

```
用户输入
   ↓
前端层 (React/TypeScript)
   ↓
API服务层 (api.ts)
   ↓
HTTP请求
   ↓
后端控制器层 (Controller)
   ↓
多智能体服务层 (MultiAgentService)
   ↓
意图分析 (OrchestratorAgent)
   ↓
Agent路由选择
   ├→ ProductExpertAgent (产品问题)
   ├→ CustomerServiceAgent (订单/服务)
   └→ OrchestratorAgent (通用对话)
   ↓
RAG服务 (RAGService)
   ↓
向量搜索 + AI生成
   ↓
响应返回
```

---

## 🔍 详细调用流程（逐步分析）

### 第1步：前端用户交互

**文件**: `aura-frontend/components/Assistant.tsx`

**方法**: `handleSend()`

**代码位置**: 第31-81行

```typescript
const handleSend = async () => {
    // 1.1 创建用户消息
    const userMsg: ChatMessage = { 
        role: 'user', 
        text: inputValue, 
        timestamp: Date.now() 
    };
    
    // 1.2 立即显示在界面上
    setMessages(prev => [...prev, userMsg]);
    
    // 1.3 准备API请求
    const request: ChatRequest = {
        message: userMsg.text,
        sessionId: sessionId
    };
    
    // 1.4 调用API服务层
    const response = await chatApi.sendMessage(request);
    
    // 1.5 显示AI响应
    const aiMsg: ChatMessage = {
        role: 'model',
        text: response.message,
        timestamp: Date.now()
    };
    setMessages(prev => [...prev, aiMsg]);
}
```

**输入**: 用户输入的文本（例如："Tell me about Aura Harmony"）

**输出**: `ChatRequest` 对象

---

### 第2步：前端API服务调用

**文件**: `aura-frontend/services/api.ts`

**方法**: `chatApi.sendMessage()`

**代码位置**: 第181-187行

```typescript
export const chatApi = {
    async sendMessage(request: ChatRequest): Promise<ChatResponse | null> {
        // 2.1 调用通用fetchApi函数
        const response = await fetchApi<ChatResponse>('/ai/chat', {
            method: 'POST',
            body: JSON.stringify(request),
        });
        
        // 2.2 返回数据
        return response.data;
    },
};
```

**HTTP请求**:
```
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "Tell me about Aura Harmony",
  "sessionId": "session_12345"
}
```

**输入**: `ChatRequest` 对象

**输出**: HTTP POST 请求到后端

---

### 第3步：后端控制器接收请求

**文件**: `aura-backend/src/main/java/com/aura/controller/AIController.java`

**方法**: `chat()`

**状态**: ❌ 未实现（成员C负责）

**预期代码**:
```java
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final MultiAgentService multiAgentService;

    /**
     * 3.1 接收前端聊天请求
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            // 3.2 调用MultiAgentService进行路由
            String aiResponse = multiAgentService.routeToAgent(
                request.getMessage(), 
                request.getSessionId()
            );
            
            // 3.3 构造响应对象
            ChatResponse response = new ChatResponse();
            response.setMessage(aiResponse);
            response.setSessionId(request.getSessionId());
            response.setTimestamp(LocalDateTime.now().toString());
            
            // 3.4 返回成功响应
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("AI chat error", e);
            return ApiResponse.error("AI服务暂时不可用");
        }
    }
}
```

**输入**: `ChatRequest` (message, sessionId)

**输出**: 调用 `MultiAgentService.routeToAgent()`

---

### 第4步：多智能体路由服务

**文件**: `aura-backend/src/main/java/com/aura/service/ai/MultiAgentService.java`

**方法**: `routeToAgent(String message, String sessionId)`

**代码位置**: 第23-51行

**状态**: ✅ 已实现

```java
public String routeToAgent(String message, String sessionId) {
    // 4.1 记录路由开始
    log.info("Routing message for session {}: {}", sessionId, message);
    
    // 4.2 调用OrchestratorAgent分析意图
    String intent = orchestratorAgent.analyzeIntent(message);
    
    // 4.3 根据意图路由到不同Agent
    String response;
    
    if (intent != null && intent.toLowerCase().contains("product")) {
        // 4.4a 路由到产品专家
        log.info("Routing to ProductExpertAgent for session {}", sessionId);
        response = productExpertAgent.handleProductInquiry(message, sessionId);
        
    } else if (intent != null && 
               (intent.toLowerCase().contains("order") || 
                intent.toLowerCase().contains("service"))) {
        // 4.4b 路由到客服
        log.info("Routing to CustomerServiceAgent for session {}", sessionId);
        response = customerServiceAgent.handleOrderInquiry(message, sessionId);
        
    } else {
        // 4.4c 通用对话由Orchestrator处理
        log.info("Handling directly by orchestrator for session {}", sessionId);
        response = orchestratorAgent.handleGeneralChat(message, sessionId);
    }
    
    // 4.5 返回响应
    log.debug("Response generated for session {}: {}", sessionId, response);
    return response;
}
```

**输入**: 
- `message`: "Tell me about Aura Harmony"
- `sessionId`: "session_12345"

**输出**: 调用 `OrchestratorAgent.analyzeIntent()`

---

### 第5步：意图分析

**文件**: `aura-backend/src/main/java/com/aura/ai/agent/OrchestratorAgent.java`

**方法**: `analyzeIntent(String message)`

**状态**: ❌ 骨架代码（成员D负责）

**预期实现**:
```java
public String analyzeIntent(String message) {
    // 5.1 构造意图分析提示词
    String prompt = String.format("""
        分析以下用户消息的意图，只返回以下类别之一：
        - PRODUCT：询问产品信息、功能、对比
        - ORDER：查询订单、修改地址、退货
        - GENERAL：问候、闲聊、其他
        
        用户消息：%s
        
        意图类别：
        """, message);
    
    // 5.2 调用ChatClient分析
    String intent = chatClient.prompt()
        .user(prompt)
        .call()
        .content();
    
    // 5.3 记录意图
    log.info("Intent analyzed: {} for message: {}", intent, message);
    
    // 5.4 返回意图（例如："PRODUCT"）
    return intent.trim();
}
```

**输入**: "Tell me about Aura Harmony"

**输出**: "PRODUCT" （意图类型）

**AI调用**: 使用 `ChatClient` → OpenAI API

---

### 第6步：Agent路由选择（回到第4步）

根据意图 "PRODUCT"，路由到 `ProductExpertAgent`

---

### 第7步：产品专家Agent处理

**文件**: `aura-backend/src/main/java/com/aura/ai/agent/ProductExpertAgent.java`

**方法**: `handleProductInquiry(String question, String sessionId)`

**状态**: ❌ 骨架代码（成员B负责）

**预期实现**:
```java
public String handleProductInquiry(String question, String sessionId) {
    // 7.1 记录处理开始
    log.info("ProductExpertAgent handling: {}", question);
    
    // 7.2 尝试从问题中提取产品ID（可选）
    String productId = extractProductId(question);
    
    // 7.3 调用RAGService查询产品手册
    String ragResponse = ragService.answerFromManual(question, productId);
    
    // 7.4 如果RAG没有足够信息，补充产品目录信息
    if (ragResponse.contains("没有找到相关信息")) {
        List<Product> products = productService.getAllProducts();
        // 构造产品列表信息
        String productList = buildProductList(products);
        
        // 7.5 结合RAG和产品列表，生成完整回答
        String prompt = String.format("""
            你是Aura的产品专家。用户问题：%s
            
            产品手册信息：%s
            
            可用产品列表：%s
            
            请提供专业、友好的回答。
            """, question, ragResponse, productList);
        
        return chatClient.prompt().user(prompt).call().content();
    }
    
    // 7.6 返回RAG响应
    return ragResponse;
}
```

**输入**: 
- `question`: "Tell me about Aura Harmony"
- `sessionId`: "session_12345"

**输出**: 调用 `RAGService.answerFromManual()`

---

### 第8步：RAG服务查询

**文件**: `aura-backend/src/main/java/com/aura/service/ai/RAGService.java`

**方法**: `answerFromManual(String question, String productId)`

**代码位置**: 第23-67行

**状态**: ✅ 已实现

```java
public String answerFromManual(String question, String productId) {
    // 8.1 记录查询
    log.info("Answering from manual: question='{}', productId='{}'", 
             question, productId);
    
    // 8.2 执行向量相似度搜索（调用第9步）
    List<Document> relevantDocs = searchSimilar(question, 3);
    
    // 8.3 检查是否找到相关文档
    if (relevantDocs.isEmpty()) {
        log.warn("No relevant documents found for question: {}", question);
        return "抱歉，我在产品手册中没有找到相关信息。";
    }
    
    // 8.4 构建上下文
    StringBuilder context = new StringBuilder();
    for (int i = 0; i < relevantDocs.size(); i++) {
        Document doc = relevantDocs.get(i);
        context.append("信息片段 ").append(i + 1).append(":\n");
        context.append(doc.getText()).append("\n\n");
    }
    
    // 8.5 构造提示词
    String prompt = String.format("""
        基于以下产品手册信息回答用户的问题。
        
        产品手册信息：
        %s
        
        用户问题：%s
        
        回答：
        """, context.toString(), question);
    
    // 8.6 调用ChatClient生成答案（调用第10步）
    String answer = chatClient.prompt()
        .user(prompt)
        .call()
        .content();
    
    // 8.7 返回答案
    log.info("Generated answer ({} chars) from {} documents", 
             answer.length(), relevantDocs.size());
    return answer;
}
```

**输入**: 
- `question`: "Tell me about Aura Harmony"
- `productId`: null 或 "p1"

**输出**: AI生成的回答

---

### 第9步：向量相似度搜索

**文件**: `aura-backend/src/main/java/com/aura/service/ai/RAGService.java`

**方法**: `searchSimilar(String query, int topK)`

**代码位置**: 第69-85行

**状态**: ✅ 已实现

```java
public List<Document> searchSimilar(String query, int topK) {
    // 9.1 记录搜索
    log.debug("Searching for similar documents: query='{}', topK={}", 
              query, topK);
    
    // 9.2 创建搜索请求
    SearchRequest searchRequest = SearchRequest.builder()
        .query(query)                    // 搜索query
        .topK(topK)                      // 返回top 3
        .similarityThreshold(0.7)        // 相似度阈值70%
        .build();
    
    // 9.3 执行向量搜索
    List<Document> results = vectorStore.similaritySearch(searchRequest);
    
    // 9.4 返回结果
    log.info("Found {} similar documents for query", results.size());
    return results;
}
```

**输入**: 
- `query`: "Tell me about Aura Harmony"
- `topK`: 3

**输出**: `List<Document>` - 3个最相关的文档片段

---

### 第10步：AI答案生成

**调用**: `ChatClient.prompt().user(prompt).call().content()`

**底层**: Spring AI → OpenAI API

**输入**: 带有上下文的prompt

**输出**: AI生成的完整回答

---

### 第11-13步：响应返回链

回答沿调用链返回到前端，最终显示在用户界面上。

---

## 📝 完整数据流示例

**输入**: "Tell me about Aura Harmony"

**输出**: "Aura Harmony is a premium wireless headphone featuring..."

**涉及13个步骤，跨越前端、后端、AI服务、向量数据库**
