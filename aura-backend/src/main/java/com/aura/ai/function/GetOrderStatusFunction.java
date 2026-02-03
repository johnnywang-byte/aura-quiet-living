package com.aura.ai.function;

import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Get Order Status Function
 * AI can call this to query order status
 */
@Component
@Description("Get order status and tracking information by order number")
@RequiredArgsConstructor
public class GetOrderStatusFunction
        implements Function<GetOrderStatusFunction.Request, GetOrderStatusFunction.Response> {

    private final OrderService orderService;

    @Override
    public Response apply(Request request) {
        // TODO: Implement
        Order order = orderService.getOrderByNumber(request.orderNumber());
        if (order == null) {
            return new Response(
                    request.orderNumber(),
                    "NOT_FOUND",
                    null,
                    ""
            );
        }
        String estimated = order.getCreatedAt() != null
                ? order.getCreatedAt().plusDays(5).toString()
                : "";
        return new Response(
                order.getOrderNumber(),
                order.getStatus() != null ? order.getStatus() : "UNKNOWN",
                order.getTrackingNumber(),
                estimated
        );
    }

    public record Request(String orderNumber) {
    }

    public record Response(
            String orderNumber,
            String status,
            String trackingNumber,
            String estimatedDelivery) {
    }
}
