package com.aura.service.ai;

import com.aura.model.entity.ChatHistory;
import com.aura.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory Service
 * Three-layer memory system: short-term, long-term, semantic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final SimpleVectorStore semanticMemory;

    // Short-term memory (in-memory)
    private final Map<String, List<ChatHistory>> shortTermMemory = new ConcurrentHashMap<>();

    /**
     * Save message to all memory layers
     */
    public void saveMessage(String sessionId, String role, String message, Map<String, Object> context) {
        // TODO: Implement
        // 1. Save to short-term memory (in-memory)
        // 2. Save to long-term memory (MySQL)
        // 3. Save to semantic memory (vector store)
    }

    /**
     * Get recent conversation history
     */
    public List<ChatHistory> getRecentHistory(String sessionId, int limit) {
        // TODO: Implement
        return null;
    }

    /**
     * Search relevant memory by semantic similarity
     */
    public List<String> searchRelevantMemory(String query, String sessionId) {
        // TODO: Implement
        return null;
    }

    /**
     * Clear session memory
     */
    public void clearSession(String sessionId) {
        // TODO: Implement
    }

    /**
     * Extract entities from context (order numbers, product IDs, etc.)
     */
    public Map<String, Object> extractEntities(String message) {
        // TODO: Implement
        return null;
    }
}
