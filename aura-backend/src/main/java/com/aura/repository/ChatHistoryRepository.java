package com.aura.repository;

import com.aura.model.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
