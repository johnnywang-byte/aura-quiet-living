package com.aura.ai.function;

import com.aura.model.entity.Order;
import com.aura.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Get Orders By Email Function
 * AI can call this to find all orders for a customer by their email
 */
@Component
@Description("Find all orders for a customer by their email address. Use this when a customer asks about their orders using their email instead of order number.")
@RequiredArgsConstructor
public class GetOrdersByEmailFunction
        implements Function<GetOrdersByEmailFunction.Request, GetOrdersByEmailFunction.Response> {

    private final OrderRepository orderRepository;

    @Override
    public Response apply(Request request) {
        List<Order> orders = orderRepository.findByCustomerEmail(request.email());

        if (orders == null || orders.isEmpty()) {
            return new Response(request.email(), List.of(), "No orders found for this email");
        }

        List<OrderInfo> orderInfos = orders.stream()
                .map(order -> new OrderInfo(
                        order.getOrderNumber(),
                        order.getStatus(),
                        order.getTotalAmount().doubleValue(),
                        order.getCreatedAt().toString(),
                        order.getShippingAddress()))
                .collect(Collectors.toList());

        return new Response(request.email(), orderInfos,
                String.format("Found %d order(s) for this email", orders.size()));
    }

    public record Request(String email) {
    }

    public record OrderInfo(
            String orderNumber,
            String status,
            double totalAmount,
            String createdAt,
            String shippingAddress) {
    }

    public record Response(
            String email,
            List<OrderInfo> orders,
            String message) {
    }
}
