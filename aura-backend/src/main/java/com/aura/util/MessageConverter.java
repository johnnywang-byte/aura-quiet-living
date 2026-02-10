package com.aura.util;

import com.aura.model.entity.ChatHistory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Message Converter Utility
 * 消息转换工具类
 * 
 * 将数据库中的 ChatHistory 实体转换为 Spring AI 的 Message 对象
 */
public class MessageConverter {

    /**
     * Convert ChatHistory entities to Spring AI Message objects
     * 
     * @param history List of ChatHistory from database
     * @return List of Spring AI Message objects
     */
    public static List<Message> convertToMessages(List<ChatHistory> history) {
        List<Message> messages = new ArrayList<>();
        
        if (history == null || history.isEmpty()) {
            return messages;
        }
        
        for (ChatHistory chat : history) {
            if ("user".equals(chat.getRole())) {
                messages.add(new UserMessage(chat.getMessage()));
            } else if ("assistant".equals(chat.getRole())) {
                messages.add(new AssistantMessage(chat.getMessage()));
            }
            // Ignore other roles (system, function, etc.)
        }
        
        return messages;
    }

    /**
     * Convert ChatHistory entities to Spring AI Message objects (with limit)
     * 
     * @param history List of ChatHistory from database
     * @param limit Maximum number of messages to convert
     * @return List of Spring AI Message objects
     */
    public static List<Message> convertToMessages(List<ChatHistory> history, int limit) {
        if (history == null || history.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Take last N messages
        int startIndex = Math.max(0, history.size() - limit);
        List<ChatHistory> limitedHistory = history.subList(startIndex, history.size());
        
        return convertToMessages(limitedHistory);
    }
}
