package com.aura.ai.agent;

import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Customer Service Agent
 * Specialized in order management and customer support
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceAgent {

    private final ChatClient chatClient;
    private final OrderService orderService;

    /**
     * Handle order inquiry
     */
    public String handleOrderInquiry(String orderNumber) {
        log.info("Handling order inquiry for order number: {}", orderNumber);

        try {
            // Get order from OrderService
            Order order = orderService.getOrderByNumber(orderNumber);

            if (order == null) {
                log.warn("Order not found: {}", orderNumber);
                return String.format("I'm sorry, I couldn't find your order with number %s. Please check the order number and try again.", orderNumber);
            }

            log.info("Order found: {}, Status: {}", orderNumber, order.getStatus());

            // Format order information into a user-friendly response
            return String.format(
                    "Here's the information for your order:\n" +
                            "Order Number: %s\n" +
                            "Status: %s\n" +
                            "Created At: %s\n" +
                            "Total Amount: $%.2f\n" +
                            "Shipping Address: %s",
                    order.getOrderNumber(),
                    order.getStatus(),
                    order.getCreatedAt().toString(),
                    order.getTotalAmount(),
                    order.getShippingAddress()
            );
        } catch (Exception e) {
            log.error("Error handling order inquiry: {}", e.getMessage(), e);
            return "I'm sorry, I encountered an error while processing your request. Please try again later.";
        }
    }

    /**
     * Update order address
     */
    public String updateOrderAddress(String orderNumber, String newAddress) {
        log.info("Updating address for order: {} to: {}", orderNumber, newAddress);

        try {
            // Get order from OrderService
            Order order = orderService.getOrderByNumber(orderNumber);

            if (order == null) {
                log.warn("Order not found for address update: {}", orderNumber);
                return String.format("I'm sorry, I couldn't find your order with number %s. Please check the order number and try again.", orderNumber);
            }

            // Check if order is in a status that allows address change
            if (!"PENDING".equals(order.getStatus()) && !"PROCESSING".equals(order.getStatus())) {
                log.warn("Cannot update address for order in status: {}, Order: {}", order.getStatus(), orderNumber);
                return String.format("I'm sorry, your order %s is currently in %s status, which doesn't allow address changes.", orderNumber, order.getStatus());
            }

            // Update the address using OrderService
            Order updatedOrder = orderService.updateShippingAddress(orderNumber, newAddress);
            log.info("Address updated successfully for order: {}", orderNumber);

            return String.format("Your order %s shipping address has been successfully updated to: %s", orderNumber, newAddress);
        } catch (Exception e) {
            log.error("Error updating order address: {}", e.getMessage(), e);
            return "I'm sorry, I encountered an error while updating your address. Please try again later.";
        }
    }

    /**
     * Handle return request
     */
    public String handleReturnRequest(String orderNumber, String reason) {
        log.info("Handling return request for order: {}, Reason: {}", orderNumber, reason);

        try {
            // Get order from OrderService
            Order order = orderService.getOrderByNumber(orderNumber);

            if (order == null) {
                log.warn("Order not found for return request: {}", orderNumber);
                return String.format("I'm sorry, I couldn't find your order with number %s. Please check the order number and try again.", orderNumber);
            }

            // Check if order is eligible for return
            if (!"DELIVERED".equals(order.getStatus())) {
                log.warn("Cannot process return for order in status: {}, Order: {}", order.getStatus(), orderNumber);
                return String.format("I'm sorry, your order %s is currently in %s status, which doesn't allow returns.", orderNumber, order.getStatus());
            }

            // Process return request
            orderService.updateOrderStatus(orderNumber, "RETURN_REQUESTED");
            log.info("Return request processed for order: {}", orderNumber);

            return String.format(
                    "I've processed your return request for order %s.\n" +
                            "Return Reason: %s\n" +
                            "Our customer service team will contact you within 1-2 business days to arrange the return.\n" +
                            "You'll receive a confirmation email shortly.",
                    orderNumber, reason
            );
        } catch (Exception e) {
            log.error("Error handling return request: {}", e.getMessage(), e);
            return "I'm sorry, I encountered an error while processing your return request. Please try again later.";
        }
    }
}
