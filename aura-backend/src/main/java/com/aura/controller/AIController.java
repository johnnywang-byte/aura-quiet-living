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
        return null;
    }

    /**
     * GET /api/ai/chat/history/{sessionId} - Get chat history
     */
    @GetMapping("/chat/history/{sessionId}")
    public ApiResponse<List<ChatResponse>> getChatHistory(@PathVariable String sessionId) {
        // TODO: Implement
        return null;
    }

    /**
     * DELETE /api/ai/chat/history/{sessionId} - Clear chat history
     */
    @DeleteMapping("/chat/history/{sessionId}")
    public ApiResponse<Void> clearChatHistory(@PathVariable String sessionId) {
        // TODO: Implement
        return null;
    }
}
