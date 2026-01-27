package com.aura.model.dto;

import lombok.Data;
import java.util.List;

/**
 * Chat Response DTO
 */
@Data
public class ChatResponse {

    private String message;
    private String sessionId;
    private List<String> suggestedProducts;
    private List<SuggestedAction> suggestedActions;
    private String timestamp;

    @Data
    public static class SuggestedAction {
        private String type;
        private String productId;
        private String label;
    }
}
