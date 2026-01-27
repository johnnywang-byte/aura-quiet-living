package com.aura.model.dto;

import lombok.Data;
import java.util.List;

/**
 * Chat Request DTO
 */
@Data
public class ChatRequest {

    private String message;
    private String sessionId;
    private ChatContext context;

    @Data
    public static class ChatContext {
        private String currentPage;
        private String viewingProductId;
        private String lastOrderNumber;
    }
}
