package com.aura.ai.function;

import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Update Order Address Function
 * 更新订单配送地址Function
 */
@Component
@Description("Update the shipping address for an existing order. Use this when a customer wants to change where their order should be delivered. Requires the order number and the new complete shipping address. Returns detailed error messages if update fails.")
@RequiredArgsConstructor
public class UpdateOrderAddressFunction
        implements Function<UpdateOrderAddressFunction.Request, UpdateOrderAddressFunction.Response> {

    private final OrderService orderService;

    @Override
    public Response apply(Request request) {
        try {
            // Validate input
            if (request.orderNumber() == null || request.orderNumber().trim().isEmpty()) {
                return new Response(false, "Order number is required", null);
            }
            if (request.newAddress() == null || request.newAddress().trim().isEmpty()) {
                return new Response(false, "New address is required", null);
            }

            // Update address
            Order updated = orderService.updateShippingAddress(request.orderNumber(), request.newAddress());
            
            return new Response(
                true, 
                "Shipping address updated successfully",
                String.format("The shipping address for order %s has been updated to: %s", 
                    request.orderNumber(), request.newAddress())
            );

        } catch (EntityNotFoundException e) {
            // Handle order not found error (thrown by getOrderByNumber)
            return new Response(
                false,
                "ORDER_NOT_FOUND",
                String.format("I'm sorry, but I couldn't find an order with number %s. " +
                    "Please double-check the order number. The format should be like: ORD-20260206081552-1500. " +
                    "If you need help, I can search for your orders by email address.", 
                    request.orderNumber())
            );

        } catch (IllegalArgumentException e) {
            // Handle validation errors (status not allowed, etc.)
            String errorMessage = e.getMessage();
            
            // Parse specific error types
            if (errorMessage.contains("status")) {
                // Order exists but status doesn't allow modification
                return new Response(
                    false, 
                    "STATUS_NOT_ALLOWED",
                    String.format("I'm sorry, but I cannot modify the shipping address for order %s " +
                        "because it has already been shipped, delivered, or cancelled. " +
                        "For orders that have been shipped, please contact our support team at support@aura.com " +
                        "or call 1-800-AURA-HELP for assistance.", 
                        request.orderNumber())
                );
            } else {
                return new Response(false, "VALIDATION_ERROR", errorMessage);
            }

        } catch (Exception e) {
            // Handle unexpected errors
            return new Response(
                false, 
                "SYSTEM_ERROR",
                String.format("An unexpected error occurred while updating the address for order %s. " +
                    "Please try again in a few moments or contact our support team.", 
                    request.orderNumber())
            );
        }
    }

    public record Request(String orderNumber, String newAddress) {
    }

    public record Response(boolean success, String message, String details) {
    }
}
