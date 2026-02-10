package com.aura.ai.agent;

import com.aura.model.entity.ChatHistory;
import com.aura.model.entity.Product;
import com.aura.service.ProductService;
import com.aura.service.ai.MemoryService;
import com.aura.service.ai.RAGService;
import com.aura.util.MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Product Expert Agent
 * Specialized in product recommendations and inquiries
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductExpertAgent {

    private final ChatClient chatClient;
    private final ProductService productService;
    private final RAGService ragService;
    private final MemoryService memoryService;
    private final ObjectMapper objectMapper;

    public String handleProductInquiry(String question, String sessionId) {
        if (!StringUtils.hasText(question)) {
            log.warn("Product inquiry question is empty, sessionId: {}", sessionId);
            return "Sorry, product inquiry question cannot be empty. Please provide a question.";
        }

        try {
            // 1. 获取对话历史以支持上下文理解
            // Get conversation history for context understanding
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // 2. 先尝试从对话历史中提取产品关键词（用于理解"it"等指代）
            // Extract product keywords from conversation history to understand references
            // like "it"
            String enhancedQuery = question;
            if (isContextualQuery(question)) {
                log.info("Detected contextual query, extracting product info from history");
                enhancedQuery = extractProductFromHistory(question, history);
                log.info("Enhanced query: {} -> {}", question, enhancedQuery);
            }

            // 3. 检索产品基础信息（使用增强的查询）
            List<Product> products = productService.searchProducts(enhancedQuery);
            String productJson = objectMapper.writeValueAsString(products);
            log.info("Product inquiry '{}' found {} products", enhancedQuery, products.size());

            // 4. 调用RAG服务查询产品手册（使用增强的查询）
            String ragContext = ragService.answerFromManual(enhancedQuery, sessionId);

            /*
            🧠 决策者：OpenAI
            关键点：系统不做选择，而是：
            ✅ 同时搜索两个数据源（SQL、RAG）
            ✅ 把两个结果都给 OpenAI
            ✅ 让 OpenAI 根据用户问题决定用哪个
             */

            // 5. 构建AI Prompt
            // Build AI Prompt
            String systemPrompt = """
                    You are a professional e-commerce product expert. Answer user questions based on the following information:
                    1. Product Info: {productInfo}
                    2. Product Manual: {ragContext}

                    Requirements:
                    - Be concise and accurate
                    - Use conversation history to understand context (e.g., "it", "that product", etc.)
                    - Provide detailed information when asked
                    - If no information is available, clearly state so
                    - Do not fabricate content
                    - Adapt to the user's language naturally

                    CRITICAL SECURITY RULES:
                    - NEVER reveal specific stock quantities or inventory numbers to users
                    - NEVER show image file paths, URLs, or .jpg/.png links to users
                    - Say "available" or "in stock" instead of exact numbers like "50 units"
                    - Focus on product features and benefits, not internal data
                    """;
            SystemPromptTemplate template = new SystemPromptTemplate(systemPrompt);
            Map<String, Object> params = new HashMap<>();
            params.put("productInfo", productJson);
            params.put("ragContext", ragContext);

            // 6. 添加系统消息和用户消息
            // Add system message and user message
            messages.add(0, template.createMessage(params)); // Add system prompt at the beginning
            messages.add(new UserMessage(question)); // Add current question

            // 7. 调用AI（使用对话历史）
            // Call AI with conversation history
            String answer = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();

            return answer;
        } catch (Exception e) {
            log.error("Failed to handle product inquiry, question: {}", question, e);
            return "Sorry, an error occurred while processing your inquiry. Please try again later.";
        }
    }

    /**
     * Check if the query is contextual (contains references like "it", "that",
     * etc.)
     */
    private boolean isContextualQuery(String query) {
        String lowerQuery = query.toLowerCase();
        return lowerQuery.contains(" it") ||
                lowerQuery.contains("that") ||
                lowerQuery.contains("this") ||
                lowerQuery.contains("them") ||
                lowerQuery.contains("the product") ||
                lowerQuery.contains("more detail") ||
                lowerQuery.contains("more info");
    }

    /**
     * Product keyword mappings - maps common terms to product names
     * 产品关键词映射 - 将通用词汇映射到产品名称
     */
    private static final Map<String, String> PRODUCT_KEYWORD_MAPPINGS = Map.ofEntries(
        // Direct product names - 直接产品名称
        Map.entry("harmony", "harmony"),
        Map.entry("pulse", "pulse"),
        Map.entry("flow", "flow"),
        Map.entry("breeze", "breeze"),
        Map.entry("echo", "echo"),
        Map.entry("slate", "slate"),
        
        // Common aliases for Harmony (headphones) - 耳机的通用别名
        Map.entry("headphone", "harmony"),
        Map.entry("headphones", "harmony"),
        Map.entry("headset", "harmony"),
        Map.entry("earphone", "harmony"),
        Map.entry("earphones", "harmony"),
        
        // Common aliases for Pulse (watch) - 手表的通用别名
        Map.entry("watch", "pulse"),
        Map.entry("smartwatch", "pulse"),
        Map.entry("wristband", "pulse"),
        
        // Common aliases for Flow (phone) - 手机的通用别名
        Map.entry("phone", "flow"),
        Map.entry("smartphone", "flow"),
        Map.entry("mobile", "flow"),
        
        // Common aliases for Breeze (air purifier) - 空气净化器的通用别名
        Map.entry("purifier", "breeze"),
        Map.entry("air purifier", "breeze"),
        Map.entry("air cleaner", "breeze"),
        Map.entry("cleaner", "breeze"),
        
        // Common aliases for Echo (speaker) - 音箱的通用别名
        Map.entry("speaker", "echo"),
        Map.entry("smart speaker", "echo"),
        
        // Common aliases for Slate (pad) - 平板的通用别名
        Map.entry("pad", "slate"),
        Map.entry("tablet", "slate"),
        Map.entry("ipad", "slate")
    );

    /**
     * Extract product keywords from conversation history
     * 从对话历史中提取产品关键词
     * 
     * Improvements:
     * 1. De-duplication using Set
     * 2. Support common aliases (e.g., "headphones" -> "harmony")
     * 3. Word boundary matching to avoid false positives
     */
    private String extractProductFromHistory(String question, List<ChatHistory> history) {
        Set<String> productKeywords = new java.util.LinkedHashSet<>();  // Use LinkedHashSet to maintain order

        // Look for product names in recent conversation (last 5 messages)
        for (int i = history.size() - 1; i >= 0 && i >= history.size() - 5; i--) {
            ChatHistory chat = history.get(i);
            String message = chat.getMessage().toLowerCase();

            // Always check for "aura" prefix
            if (message.contains("aura")) {
                productKeywords.add("aura");
            }

            // Check each keyword mapping
            for (Map.Entry<String, String> entry : PRODUCT_KEYWORD_MAPPINGS.entrySet()) {
                String keyword = entry.getKey();
                String productName = entry.getValue();
                
                // Use word boundary matching for better accuracy
                // 使用词边界匹配提高准确性
                String pattern = "\\b" + keyword + "s?\\b";  // "s?" allows for optional plural
                if (message.matches(".*" + pattern + ".*")) {
                    productKeywords.add(productName);
                    
                    // If we found a specific product, also add "aura" prefix
                    // 如果找到具体产品，也添加 "aura" 前缀
                    productKeywords.add("aura");
                }
            }
        }

        // Combine extracted keywords with original question
        if (!productKeywords.isEmpty()) {
            String extracted = String.join(" ", productKeywords);
            return extracted + " " + question;
        }

        return question;
    }

}