package com.aura.repository;

import com.aura.model.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Chat History Repository
 */
@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    /**
     * Find chat history by session ID ordered by creation time
     */
    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * Find recent chat history by session ID
     */
    List<ChatHistory> findTop10BySessionIdOrderByCreatedAtDesc(String sessionId);

    /**
     * Delete chat history by session ID
     */
    void deleteBySessionId(String sessionId);

    /**
     * Find chat history by session ID and role
     */
    List<ChatHistory> findBySessionIdAndRoleOrderByCreatedAtAsc(String sessionId, String role);

    /**
     * Find chat history within a time range
     */
    List<ChatHistory> findByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find chat history by session ID within a time range
     */
    List<ChatHistory> findBySessionIdAndCreatedAtBetweenOrderByCreatedAtAsc(String sessionId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get the total count of chat history records
     */
    long count();

    /**
     * Get the count of chat history records by session ID
     */
    long countBySessionId(String sessionId);

    /**
     * Find all distinct session IDs
     */
    List<String> findDistinctSessionIdBy();

    /**
     * Find chat history by message content containing a keyword
     */
    List<ChatHistory> findByMessageContainingIgnoreCaseOrderByCreatedAtAsc(String keyword);

    /**
     * Find chat history by session ID and message content containing a keyword
     */
    List<ChatHistory> findBySessionIdAndMessageContainingIgnoreCaseOrderByCreatedAtAsc(String sessionId, String keyword);

    /**
     * Find the first chat history record by session ID (oldest message)
     */
    Optional<ChatHistory> findFirstBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * Find the last chat history record by session ID (most recent message)
     */
    Optional<ChatHistory> findFirstBySessionIdOrderByCreatedAtDesc(String sessionId);

    /**
     * Count chat history records by date
     */
    @Query("SELECT DATE(h.createdAt), COUNT(h) FROM ChatHistory h GROUP BY DATE(h.createdAt) ORDER BY DATE(h.createdAt)")
    List<Object[]> countByDate();
}