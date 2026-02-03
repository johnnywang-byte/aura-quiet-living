package com.aura.model.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Chat History Entity
 */
@Entity
@Table(name = "chat_history")
@Data
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "context_data", columnDefinition = "JSON")
    private String contextDataJson;

    @Transient
    private Map<String, Object> contextData;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // ObjectMapper for JSON conversion
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Logger instance
    private static final Logger logger = LoggerFactory.getLogger(ChatHistory.class);


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        convertContextDataToJson();
    }

    @PreUpdate
    protected void onUpdate() {
        convertContextDataToJson();
    }

    /**
     * Convert contextData Map to JSON string for database storage
     */
    private void convertContextDataToJson() {
        if (contextData != null) {
            try {
                contextDataJson = objectMapper.writeValueAsString(contextData);
            } catch (JsonProcessingException e) {
                logger.error("Failed to convert contextData to JSON: {}", e.getMessage(), e);
                contextDataJson = "{}"; // Default to empty JSON object on error
            }
        } else {
            contextDataJson = null;
        }
    }

    /**
     * Get contextData Map from JSON string
     * Lazy loads the context data from contextDataJson
     */
    public Map<String, Object> getContextData() {
        if (contextData == null && contextDataJson != null) {
            try {
                contextData = objectMapper.readValue(contextDataJson, Map.class);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse contextDataJson: {}", e.getMessage(), e);
                contextData = new HashMap<>(); // Return empty map on error
            }
        }
        return contextData;
    }

    /**
     * Set contextData Map and update contextDataJson
     */
    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
        convertContextDataToJson(); // Update JSON immediately
    }

    /**
     * Get contextDataJson string
     */
    public String getContextDataJson() {
        // Ensure JSON is up-to-date with current Map before returning
        if (contextData != null) {
            convertContextDataToJson();
        }
        return contextDataJson;
    }

    /**
     * Set contextDataJson string and clear contextData Map to force lazy loading
     */
    public void setContextDataJson(String contextDataJson) {
        this.contextDataJson = contextDataJson;
        this.contextData = null; // Clear Map to force lazy loading from new JSON
    }


}