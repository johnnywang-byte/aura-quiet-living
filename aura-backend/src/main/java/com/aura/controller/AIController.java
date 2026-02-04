package com.aura.controller;

import com.aura.model.dto.ApiResponse;
import com.aura.model.dto.ChatRequest;
import com.aura.model.dto.ChatResponse;
import com.aura.service.ai.MultiAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * AI Controller
 * Handles AI chat interactions and routes to multi-agent system
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final MultiAgentService multiAgentService;

    /**
     * POST /api/ai/chat - Send message to AI agent
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: sessionId={}, message='{}'",
                    request.getSessionId(), request.getMessage());

            // Route to appropriate agent via MultiAgentService
            String aiResponse = multiAgentService.routeToAgent(
                    request.getMessage(),
                    request.getSessionId());

            // Construct response
            ChatResponse response = new ChatResponse();
            response.setMessage(aiResponse);
            response.setSessionId(request.getSessionId());
            response.setTimestamp(LocalDateTime.now().toString());

            log.info("Chat response generated: sessionId={}, length={}",
                    request.getSessionId(), aiResponse.length());

            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Chat error: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage(), e);
            return ApiResponse.error("AI service temporarily unavailable: " + e.getMessage());
        }
    }
}
