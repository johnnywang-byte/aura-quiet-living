package com.aura.ai.function;

import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Cancel Order Function
 * 取消订单Function
 * 
 * 业务规则：
 * - PENDING 状态可以直接取消
 * - SHIPPED/DELIVERED 状态需要转接人工客服
 */
@Component
@Description("Cancel a pending order. Use this when a customer wants to cancel their order. " +
             "Only PENDING orders can be cancelled. For SHIPPED or DELIVERED orders, guide customer to contact support.")
@RequiredArgsConstructor
@Slf4j
public class CancelOrderFunction implements Function<CancelOrderFunction.Request, CancelOrderFunction.Response> {

    private final OrderService orderService;

    @Override
    public Response apply(Request request) {
        try {
            // Validate input
            if (request.orderNumber() == null || request.orderNumber().trim().isEmpty()) {
                return new Response(
                    false, 
                    "INVALID_INPUT",
                    "Please provide a valid order number."
                );
            }

            log.info("Attempting to cancel order: {}", request.orderNumber());

            // Cancel the order (updateOrderStatus will query and validate status internally)
            orderService.updateOrderStatus(request.orderNumber(), "CANCELLED");
            
            log.info("Order cancelled successfully: {}", request.orderNumber());
            return new Response(
                true,
                "ORDER_CANCELLED",
                String.format("Your order %s has been successfully cancelled. " +
                    "The payment will be refunded within 3-5 business days. " +
                    "Any reserved stock has been released.",
                    request.orderNumber())
            );

        } catch (EntityNotFoundException e) {
            // Order not found
            log.warn("Order not found for cancellation: {}", request.orderNumber());
            return new Response(
                false,
                "ORDER_NOT_FOUND",
                String.format("I'm sorry, but I couldn't find an order with number %s. " +
                    "Please double-check the order number. The format should be like: ORD-20260206081552-1500.",
                    request.orderNumber())
            );

        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            
            // Check if it's a status-related error (SHIPPED, DELIVERED, etc.)
            if (errorMessage.contains("ORDER_ALREADY_SHIPPED")) {
                log.info("Order already shipped, requires manual service: {}", request.orderNumber());
                return new Response(
                    false,
                    "REQUIRES_MANUAL_SERVICE",
                    String.format("Your order %s has already been shipped and cannot be cancelled automatically. " +
                        "Please contact our customer service team:\n" +
                        "📧 Email: support@aura.com\n" +
                        "📞 Phone: 1-800-AURA-HELP\n" +
                        "Our team will assist you with return or exchange options.",
                        request.orderNumber())
                );
            } else if (errorMessage.contains("ORDER_ALREADY_DELIVERED")) {
                log.info("Order already delivered, requires manual service: {}", request.orderNumber());
                return new Response(
                    false,
                    "REQUIRES_MANUAL_SERVICE",
                    String.format("Your order %s has already been delivered. " +
                        "If you need to return the product, please contact our customer service team:\n" +
                        "📧 Email: support@aura.com\n" +
                        "📞 Phone: 1-800-AURA-HELP\n" +
                        "They will guide you through the return process.",
                        request.orderNumber())
                );
            } else if (errorMessage.contains("ORDER_ALREADY_CANCELLED")) {
                log.info("Order already cancelled: {}", request.orderNumber());
                return new Response(
                    false,
                    "ALREADY_CANCELLED",
                    String.format("Order %s has already been cancelled. " +
                        "If you need to place a new order, please visit our product page.",
                        request.orderNumber())
                );
            } else {
                // Other validation errors
                return new Response(
                    false,
                    "VALIDATION_ERROR",
                    errorMessage
                );
            }

        } catch (Exception e) {
            // Unexpected errors
            log.error("Unexpected error cancelling order: {}", request.orderNumber(), e);
            return new Response(
                false,
                "SYSTEM_ERROR",
                String.format("An unexpected error occurred while cancelling order %s. " +
                    "Please try again or contact our support team.",
                    request.orderNumber())
            );
        }
    }

    public record Request(String orderNumber, String reason) {}
    
    public record Response(boolean success, String code, String message) {}
}
