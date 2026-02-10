package com.aura.service.ai;

import com.aura.ai.agent.OrchestratorAgent;
import com.aura.model.dto.ChatRequest;
import com.aura.model.dto.ChatResponse;
import com.aura.model.entity.ChatHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI Agent Service
 * AI服务入口
 * 
 * 职责：
 * - 统一的业务编排层
 * - 处理完整的对话流程
 * - 保存对话历史
 * - 调用 OrchestratorAgent 进行路由
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIAgentService {

    private final MemoryService memoryService;
    private final OrchestratorAgent orchestratorAgent;

    /**
     * Process chat message
     * 处理聊天消息（统一入口）
     * 
     * 职责：
     * 1. 验证输入
     * 2. 提取实体
     * 3. 保存用户消息
     * 4. 路由到OrchestratorAgent
     * 5. 保存AI响应
     * 6. 返回响应
     * 
     * @param request Chat request
     * @return Chat response
     */
    public ChatResponse processMessage(ChatRequest request) {
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();

        log.info("Processing message for session: {}", sessionId);

        // Validate input
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        try {
            // 1. Extract entities from user message
            var entities = memoryService.extractEntities(userMessage);
            log.info("Extracted entities: {}", entities);

            // 2. Save user message to memory (before processing)
            memoryService.saveMessage(sessionId, "user", userMessage, entities);

            // 3. Route to orchestrator agent
            // OrchestratorAgent will:
            // - Analyze intent
            // - Route to appropriate specialized agent
            // - Return response
            String responseContent = orchestratorAgent.routeMessage(userMessage, sessionId);// ⚠️ ⚠️ ⚠️每条消息都调用OrchestratorAgent进行路由

            // 4. Save AI response to memory
            memoryService.saveMessage(sessionId, "assistant", responseContent, 
                    java.util.Map.of("entities", entities));

            // 5. Create and return ChatResponse
            ChatResponse response = new ChatResponse();
            response.setSessionId(sessionId);
            response.setMessage(responseContent);
            response.setTimestamp(java.time.LocalDateTime.now().toString());

            log.info("Message processed successfully for session: {}", sessionId);
            return response;

        } catch (Exception e) {
            log.error("Error processing message for session {}: {}", sessionId, e.getMessage(), e);

            // Create error response
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(sessionId);
            errorResponse.setMessage(
                    "I'm sorry, I encountered an error while processing your request. Please try again later.");
            errorResponse.setTimestamp(java.time.LocalDateTime.now().toString());

            return errorResponse;
        }
    }

    /**
     * Get chat history
     */
    public java.util.List<ChatResponse> getChatHistory(String sessionId) {
        log.info("Retrieving chat history for session: {}", sessionId);

        try {
            // Get all chat history for the session from MemoryService
            java.util.List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 100);

            // Convert to ChatResponse list
            return history.stream()
                    .map(chatHistory -> {
                        ChatResponse response = new ChatResponse();
                        response.setSessionId(sessionId);
                        response.setMessage(chatHistory.getMessage());
                        response.setTimestamp(chatHistory.getCreatedAt().toString());

                        return response;
                    })
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("Error retrieving chat history: {}", e.getMessage(), e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Clear chat history
     */
    public void clearChatHistory(String sessionId) {
        log.info("Clearing chat history for session: {}", sessionId);

        try {
            memoryService.clearSession(sessionId);
            log.info("Chat history cleared successfully for session: {}", sessionId);
        } catch (Exception e) {
            log.error("Error clearing chat history: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear chat history", e);
        }
    }
}