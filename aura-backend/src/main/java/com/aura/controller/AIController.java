package com.aura.controller;

import com.aura.model.dto.ApiResponse;
import com.aura.model.dto.ChatRequest;
import com.aura.model.dto.ChatResponse;
import com.aura.service.ai.AIAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI Controller
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIAgentService aiAgentService;

    /**
     * POST /api/ai/chat - Send message to AI agent
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        // TODO: Implement
        try {
            ChatResponse response = aiAgentService.processMessage(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * GET /api/ai/chat/history/{sessionId} - Get chat history
     */
    @GetMapping("/chat/history/{sessionId}")
    public ApiResponse<List<ChatResponse>> getChatHistory(@PathVariable String sessionId) {
        // TODO: Implement
        try {
            List<ChatResponse> list = aiAgentService.getChatHistory(sessionId);
            return ApiResponse.success(list != null ? list : List.of());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * DELETE /api/ai/chat/history/{sessionId} - Clear chat history
     */
    @DeleteMapping("/chat/history/{sessionId}")
    public ApiResponse<Void> clearChatHistory(@PathVariable String sessionId) {
        // TODO: Implement
        try {
            aiAgentService.clearChatHistory(sessionId);
            return ApiResponse.success(null, "已清空");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
