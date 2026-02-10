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
 * General Chat Agent
 * 通用对话Agent
 * 
 * 职责：
 * - 处理闲聊和通用问题
 * - 友好的对话交互
 * - 不涉及产品或订单的业务逻辑
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeneralChatAgent {

    private final ChatClient chatClient;
    private final MemoryService memoryService;

    /**
     * System prompt for general chat
     */
    private static final String GENERAL_CHAT_SYSTEM_PROMPT = """
            You are Aura, a friendly and helpful AI assistant for Aura Quiet Living, 
            an e-commerce platform selling high-quality electronic products.
            
            Your role:
            - Engage in friendly, natural conversations
            - Answer general questions
            - Provide helpful information when possible
            - If the user asks about products or orders, politely guide them to ask specific questions
            
            Guidelines:
            - Be warm, friendly, and professional
            - Keep responses concise but informative
            - Adapt to the user's language naturally
            - If you don't know something, admit it honestly
            - Do not fabricate product or order information
            """;

    /**
     * Handle general chat messages
     * 处理通用对话消息
     * 
     * @param message User's message
     * @param sessionId Session ID for conversation context
     * @return AI response
     */
    public String handleGeneralChat(String message, String sessionId) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for general chat, sessionId: {}", sessionId);
            return "I'm here to help! How can I assist you today?";
        }

        try {
            log.info("Handling general chat for session: {}", sessionId);

            // 1. Get conversation history for context
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // 2. Add current user message
            messages.add(new UserMessage(message));

            // 3. Call AI with system prompt and conversation history
            String response = chatClient.prompt()
                    .system(GENERAL_CHAT_SYSTEM_PROMPT)
                    .messages(messages)
                    .call()
                    .content();

            log.info("General chat response generated for session: {}", sessionId);
            return response;

        } catch (Exception e) {
            log.error("Error handling general chat for session {}: {}", sessionId, e.getMessage(), e);
            return "I apologize, but I'm having trouble processing your message right now. " +
                   "Could you please try again?";
        }
    }
}
