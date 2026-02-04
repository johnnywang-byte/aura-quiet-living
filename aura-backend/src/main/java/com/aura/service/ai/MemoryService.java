package com.aura.service.ai;

import com.aura.model.entity.ChatHistory;
import com.aura.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        // Create ChatHistory entity
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSessionId(sessionId);
        chatHistory.setRole(role);
        chatHistory.setMessage(message);
        chatHistory.setContextData(context);

        // 1. Save to long-term memory (MySQL)
        chatHistory = chatHistoryRepository.save(chatHistory);

        // Make chatHistory effectively final for lambda expression
        final ChatHistory finalChatHistory = chatHistory;

        // 2. Save to short-term memory (in-memory)
        shortTermMemory.compute(sessionId, (key, existingHistory) -> {
            List<ChatHistory> updatedHistory = existingHistory != null ? new ArrayList<>(existingHistory) : new ArrayList<>();
            updatedHistory.add(finalChatHistory);
            // Keep short-term memory limited to 50 messages per session
            if (updatedHistory.size() > 50) {
                updatedHistory = updatedHistory.subList(updatedHistory.size() - 50, updatedHistory.size());
            }
            return updatedHistory;
        });

        // 3. Save to semantic memory (vector store)
        try {
            // Create metadata map
            Map<String, Object> metadata = Map.of(
                    "sessionId", sessionId,
                    "role", role,
                    "timestamp", chatHistory.getCreatedAt().toString()
            );

            // Create document using constructor
            Document document = new Document(chatHistory.getMessage(), metadata);

            // Add document to vector store (accepts List<Document>)
            semanticMemory.add(List.of(document));
        } catch (Exception e) {
            log.error("Failed to save to semantic memory: {}", e.getMessage(), e);
        }

        log.info("Message saved to memory layers for session: {}", sessionId);
    }

    /**
     * Get recent conversation history
     */
    public List<ChatHistory> getRecentHistory(String sessionId, int limit) {
        // Try to get from short-term memory first (faster)
        List<ChatHistory> recentHistory = shortTermMemory.get(sessionId);

        if (recentHistory != null && recentHistory.size() >= limit) {
            // Return last 'limit' messages from short-term memory
            int startIndex = Math.max(0, recentHistory.size() - limit);
            return recentHistory.subList(startIndex, recentHistory.size());
        }

        // Fall back to long-term memory (MySQL)
        return chatHistoryRepository.findTop10BySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Search relevant memory by semantic similarity
     */
    public List<String> searchRelevantMemory(String query, String sessionId) {
        try {
            // Search semantic memory with query using non-deprecated API
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .similarityThreshold(0.7)
                    .build();

            List<Document> relevantDocs = semanticMemory.similaritySearch(searchRequest);

            // Filter by sessionId if provided and relevantDocs is not null
            if (sessionId != null && relevantDocs != null) {
                relevantDocs = relevantDocs.stream()
                        .filter(doc -> sessionId.equals(doc.getMetadata().get("sessionId")))
                        .toList(); // Use toList() instead of collect(Collectors.toList())
            }

            // Extract message content from relevant documents
            if (relevantDocs != null) {
                return relevantDocs.stream()
                        .map(Document::getText) // Use getText() instead of deprecated getContent()
                        .toList();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to search relevant memory: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Clear session memory
     */
    public void clearSession(String sessionId) {
        // 1. Clear short-term memory
        shortTermMemory.remove(sessionId);

        // 2. Clear long-term memory
        chatHistoryRepository.deleteBySessionId(sessionId);

        // 3. Note: Semantic memory doesn't support session-level deletion easily
        // We would need to iterate through all documents and delete by sessionId
        // This is omitted for performance reasons, but could be implemented if needed

        log.info("Session memory cleared: {}", sessionId);
    }

    /**
     * Extract entities from context (order numbers, product IDs, etc.)
     */
    public Map<String, Object> extractEntities(String message) {
        Map<String, Object> entities = new HashMap<>();

        // Simple regex-based entity extraction

        // Extract order numbers (pattern: ORD-YYYYMMDD-XXXX)
        Pattern orderPattern = Pattern.compile("ORD-\\d{8}-\\d{4}");
        Matcher orderMatcher = orderPattern.matcher(message);
        List<String> orderNumbers = new ArrayList<>();
        while (orderMatcher.find()) {
            orderNumbers.add(orderMatcher.group());
        }
        if (!orderNumbers.isEmpty()) {
            entities.put("orderNumbers", orderNumbers);
        }

        // Extract product IDs (pattern: P-XXXX)
        Pattern productPattern = Pattern.compile("P-\\d{4}");
        Matcher productMatcher = productPattern.matcher(message);
        List<String> productIds = new ArrayList<>();
        while (productMatcher.find()) {
            productIds.add(productMatcher.group());
        }
        if (!productIds.isEmpty()) {
            entities.put("productIds", productIds);
        }

        // Extract email addresses
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher emailMatcher = emailPattern.matcher(message);
        List<String> emails = new ArrayList<>();
        while (emailMatcher.find()) {
            emails.add(emailMatcher.group());
        }
        if (!emails.isEmpty()) {
            entities.put("emails", emails);
        }

        // Extract phone numbers (pattern: 11-digit Chinese phone numbers)
        Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
        Matcher phoneMatcher = phonePattern.matcher(message);
        List<String> phoneNumbers = new ArrayList<>();
        while (phoneMatcher.find()) {
            phoneNumbers.add(phoneMatcher.group());
        }
        if (!phoneNumbers.isEmpty()) {
            entities.put("phoneNumbers", phoneNumbers);
        }

        return entities;
    }
}