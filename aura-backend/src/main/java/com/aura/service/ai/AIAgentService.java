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
 * Main orchestrator for AI interactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIAgentService {

    private final ChatClient chatClient;
    private final RAGService ragService;
    private final MemoryService memoryService;
    private final MultiAgentService multiAgentService;
    private final OrchestratorAgent orchestratorAgent;

    /**
     * Process chat message
     */
    public ChatResponse processMessage(ChatRequest request) {
        log.info("Processing message for session: {}", request.getSessionId());

        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();

        // Validate input
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        try {
            // 1. Analyze intent once (avoid duplicate API call)
            String intent = orchestratorAgent.analyzeIntent(userMessage);
            log.info("Analyzed intent: {}", intent);

            // 2. Extract entities from user message
            var entities = memoryService.extractEntities(userMessage);
            log.info("Extracted entities: {}", entities);

            // 3. Get recent conversation history for context
            var recentHistory = memoryService.getRecentHistory(sessionId, 10);
            log.info("Retrieved recent history: {} messages", recentHistory.size());

            // 4. Route to orchestrator agent with cached intent
            String responseContent = orchestratorAgent.routeMessage(userMessage, sessionId);

            // 5. Save user message to memory
            memoryService.saveMessage(sessionId, "user", userMessage, entities);

            // 6. Save AI response to memory (using cached intent, no duplicate API call)
            memoryService.saveMessage(sessionId, "assistant", responseContent, java.util.Map.of(
                    "intent", intent, // ✅ Use cached intent instead of calling again
                    "entities", entities));

            // 6. Create and return ChatResponse
            ChatResponse response = new ChatResponse();
            response.setSessionId(sessionId);
            response.setMessage(responseContent);
            response.setTimestamp(java.time.LocalDateTime.now().toString());

            log.info("Message processed successfully for session: {}", sessionId);
            return response;

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);

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