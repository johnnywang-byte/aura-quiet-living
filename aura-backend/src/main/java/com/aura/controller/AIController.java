package com.aura.controller;

import com.aura.model.dto.ApiResponse;
import com.aura.model.dto.ChatRequest;
import com.aura.model.dto.ChatResponse;
import com.aura.service.ai.AIAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI Controller
 * AI控制器
 * 
 * 职责：
 * - 处理HTTP请求
 * - 参数验证
 * - 调用 AIAgentService 处理业务逻辑
 * - 返回HTTP响应
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AIAgentService aiAgentService;

    /**
     * POST /api/ai/chat - Send message to AI agent
     * 发送消息给AI助手
     * 
     * @param request Chat request with message and session ID
     * @return API response with chat response
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            // 1. Validate and prepare session ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = java.util.UUID.randomUUID().toString();
                request.setSessionId(sessionId);
                log.info("Generated new session ID: {}", sessionId);
            }

            log.info("Received chat request: sessionId={}, message='{}'",
                    sessionId, request.getMessage());

            // 2. Process message via AIAgentService
            // AIAgentService will:
            // - Analyze intent
            // - Route to appropriate agent
            // - Save conversation history
            // - Return response
            ChatResponse response = aiAgentService.processMessage(request);

            log.info("Chat response generated: sessionId={}, length={}",
                    response.getSessionId(), response.getMessage().length());

            return ApiResponse.success(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid chat request: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage());
            return ApiResponse.error("Invalid request: " + e.getMessage());

        } catch (Exception e) {
            log.error("Chat error: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage(), e);
            return ApiResponse.error("AI service temporarily unavailable. Please try again later.");
        }
    }

    /**
     * GET /api/ai/history/{sessionId} - Get chat history
     * 获取对话历史
     * 
     * @param sessionId Session ID
     * @return Chat history for the session
     */
    @GetMapping("/history/{sessionId}")
    public ApiResponse<java.util.List<ChatResponse>> getChatHistory(@PathVariable String sessionId) {
        try {
            log.info("Retrieving chat history for session: {}", sessionId);
            java.util.List<ChatResponse> history = aiAgentService.getChatHistory(sessionId);
            return ApiResponse.success(history);

        } catch (Exception e) {
            log.error("Error retrieving chat history: sessionId={}, error={}",
                    sessionId, e.getMessage(), e);
            return ApiResponse.error("Failed to retrieve chat history: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/ai/history/{sessionId} - Clear chat history
     * 清除对话历史
     * 
     * @param sessionId Session ID
     * @return Success response
     */
    @DeleteMapping("/history/{sessionId}")
    public ApiResponse<String> clearChatHistory(@PathVariable String sessionId) {
        try {
            log.info("Clearing chat history for session: {}", sessionId);
            aiAgentService.clearChatHistory(sessionId);
            return ApiResponse.success("Chat history cleared successfully");

        } catch (Exception e) {
            log.error("Error clearing chat history: sessionId={}, error={}",
                    sessionId, e.getMessage(), e);
            return ApiResponse.error("Failed to clear chat history: " + e.getMessage());
        }
    }
}
