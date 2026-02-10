package com.aura.ai.agent;

import com.aura.model.entity.ChatHistory;
import com.aura.service.ai.MemoryService;
import com.aura.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Orchestrator Agent
 * 协调器Agent
 * 
 * 职责：
 * - 分析用户意图（Intent Classification）
 * - 路由消息到对应的专业Agent
 * - 不处理任何业务逻辑
 * 
 * 核心原则：只做路由，不做业务
 */
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
     * Intent classification prompt template
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

    /**
     * Analyze user intent with conversation context
     */
    public String analyzeIntent(String message, String sessionId) {
        // Validate input
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for intent analysis");
            return "UNKNOWN";
        }

        log.info("Analyzing intent for message: {}", message);

        try {
            // Retrieve recent chat history for context
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 5);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // Create prompt template for intent classification
            String promptString = INTENT_PROMPT_TEMPLATE.replace("{message}", message);

            // Add current message
            messages.add(new UserMessage(promptString));

            String intent = chatClient.prompt()
                    .messages(messages)     // ← 1. 传入消息列表
                    .call()                 // ← 2. 调用OpenAI API
                    .content()              // ← 3. 提取响应内容
                    .trim();                // ← 4. 去除首尾空格

            // Validate intent result
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

    /**
     * Route message to appropriate agent based on intent
     * 根据意图路由消息到对应的Agent
     * 
     * 这是一个纯路由方法，不处理任何业务逻辑
     * 
     * @param message   User's message
     * @param sessionId Session ID
     * @return Response from the appropriate agent
     */
    public String routeMessage(String message, String sessionId) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for routing, sessionId: {}", sessionId);
            return "I'm here to help! Please tell me what you need.";
        }

        try {
            // 1. Analyze intent (classify user's intention)
            String intent = analyzeIntent(message, sessionId);
            log.info("Intent classified as: {} for session: {}", intent, sessionId);

            // 2. Route to appropriate agent based on intent
            // 根据意图路由到对应的专业Agent
            switch (intent) {
                case "PRODUCT_INQUIRY":
                    log.info("╔═══════════════════════════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: ProductExpertAgent                             ║");
                    log.info("║ Session: {}                 ║", sessionId);
                    log.info("╚═══════════════════════════════════════════════════════════════╝");
                    // Delegate to ProductExpertAgent
                    return productExpertAgent.handleProductInquiry(message, sessionId);

                case "ORDER_SERVICE":
                    log.info("╔═══════════════════════════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: CustomerServiceAgent                           ║");
                    log.info("║ Session: {}                 ║", sessionId);
                    log.info("╚═══════════════════════════════════════════════════════════════╝");
                    // Delegate to CustomerServiceAgent
                    return customerServiceAgent.handleCustomerService(message, sessionId);

                case "GENERAL_CHAT":
                    log.info("╔═══════════════════════════════════════════════════════════════╗");
                    log.info("║ 🎯 ROUTING TO: GeneralChatAgent                               ║");
                    log.info("║ Session: {}                 ║", sessionId);
                    log.info("╚═══════════════════════════════════════════════════════════════╝");
                    // Delegate to GeneralChatAgent
                    return generalChatAgent.handleGeneralChat(message, sessionId);

                case "UNKNOWN":
                default:
                    log.warn("Unknown intent for message: {}", message);
                    // Fallback to general chat for unknown intents
                    return generalChatAgent.handleGeneralChat(message, sessionId);
            }

        } catch (Exception e) {
            log.error("Error routing message for session {}: {}", sessionId, e.getMessage(), e);
            return "I apologize, but I'm having trouble processing your request right now. " +
                    "Please try again or rephrase your question.";
        }
    }

}
