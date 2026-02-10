package com.aura.ai.agent;

import com.aura.model.entity.ChatHistory;
import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import com.aura.service.ai.MemoryService;
import com.aura.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Customer Service Agent
 * 客服Agent
 * 
 * 职责：
 * - 处理订单查询
 * - 处理订单修改（地址更新等）
 * - 处理退换货请求
 * - 协调 Function Calling（AI自动调用订单相关Functions）
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceAgent {

    private final ChatClient chatClient;
    private final OrderService orderService;
    private final MemoryService memoryService;

    /**
     * System prompt for customer service
     * 客服系统提示词
     */
    private static final String CUSTOMER_SERVICE_SYSTEM_PROMPT = """
            You are a professional customer service agent for Aura Quiet Living.
            You help customers with their orders, shipping, returns, and general customer service inquiries.

            Your capabilities:
            - Use getOrderStatusFunction to check order status and tracking
            - Use updateOrderAddressFunction to change shipping addresses
            - Use getOrdersByEmailFunction to find orders by customer email
            - Use cancelOrderFunction to cancel PENDING orders
            - Use checkInventoryFunction to verify product availability

            Guidelines:
            - Be empathetic and professional
            - Provide clear, accurate information
            - If you need to call a function, do so proactively
            - Adapt to the user's language naturally
            
            CRITICAL SECURITY RULES:
            - NEVER reveal specific stock quantities or inventory numbers to users
            - NEVER show image file paths, URLs, or .jpg/.png links to users
            - Say "available" or "in stock" instead of exact numbers
            - Protect customer privacy and system information

            IMPORTANT - Handling "Order Not Found":
            - When getOrderStatusFunction returns status="NOT_FOUND", clearly tell the user:
              "I'm sorry, but I couldn't find an order with number [ORDER_NUMBER]."
            - Then suggest helpful next steps:
              * "Please double-check the order number"
              * "The format should be like: ORD-20260206081552-1500"
              * "If you need help finding your order, I can search by your email address"

            IMPORTANT - Handling Address Update:
            - When updateOrderAddressFunction returns success=true:
              * Confirm: "I've successfully updated the shipping address for order [ORDER_NUMBER]."
              * Show the new address clearly from the 'details' field
              * Reassure: "The change will take effect immediately."

            - When updateOrderAddressFunction returns success=false:
              * Check the 'message' field for error type:
                - "ORDER_NOT_FOUND": The order doesn't exist
                - "STATUS_NOT_ALLOWED": Order has been shipped/delivered/cancelled
                - "VALIDATION_ERROR": Input validation failed
                - "SYSTEM_ERROR": System error occurred
              * ALWAYS use the information from the 'details' field to respond
              * The 'details' field contains the complete, user-friendly explanation
              * Simply present this information naturally without adding phrases like:
                - "due to a system error" ❌
                - "I'm having trouble" ❌
                - "Please try again later" ❌ (unless details says so)
              * BE DIRECT: Just relay what the 'details' field says

            IMPORTANT - Handling Order Cancellation:
            - When cancelOrderFunction returns success=true:
              * Confirm: "I've successfully cancelled your order [ORDER_NUMBER]."
              * Mention: "Payment refund within 3-5 business days"
              * Reassure: "Stock has been released"
            
            - When cancelOrderFunction returns code="REQUIRES_MANUAL_SERVICE":
              * The order is SHIPPED or DELIVERED
              * Simply relay the message from the function response
              * The message contains contact information (email, phone)
              * Be empathetic and helpful
            
            - When cancelOrderFunction returns code="ALREADY_CANCELLED":
              * Tell customer the order is already cancelled
              * Offer to help with a new order if needed

            General Rules:
            - NEVER use generic error messages
            - ALWAYS use the specific information from function responses
            - Provide actionable next steps based on the error type
            - For SHIPPED/DELIVERED orders, always guide to manual customer service
            - Adapt to the user's language naturally
            """;

    /**
     * Handle customer service requests
     * 处理客服请求（主要入口方法）
     * 
     * @param message   User's message
     * @param sessionId Session ID for conversation context
     * @return AI response with potential function calls
     */
    public String handleCustomerService(String message, String sessionId) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for customer service, sessionId: {}", sessionId);
            return "How can I assist you with your order today?";
        }

        try {
            log.info("Handling customer service request for session: {}", sessionId);

            // 1. Get conversation history for context
            List<ChatHistory> history = memoryService.getRecentHistory(sessionId, 10);
            List<Message> messages = MessageConverter.convertToMessages(history);

            // 2. Add current user message
            messages.add(new UserMessage(message));

            // 3. Call AI with Function Calling enabled
            // AI will automatically call functions like:
            // - getOrderStatusFunction
            // - updateOrderAddressFunction
            // - getOrdersByEmailFunction
            // - checkInventoryFunction
            String response = chatClient.prompt()
                    .system(CUSTOMER_SERVICE_SYSTEM_PROMPT)
                    .messages(messages)
                    .call()
                    .content();

            log.info("Customer service response generated for session: {}", sessionId);
            return response;

        } catch (Exception e) {
            log.error("Error handling customer service request for session {}: {}",
                    sessionId, e.getMessage(), e);
            return "I apologize for the inconvenience. I'm having trouble processing your request. " +
                    "Please try again or contact our support team directly.";
        }
    }

}
