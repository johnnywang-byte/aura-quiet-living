package com.aura.ai.function;

import com.aura.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Update Order Address Function
 */
@Component
@Description("Update shipping address for an order")
@RequiredArgsConstructor
public class UpdateOrderAddressFunction
        implements Function<UpdateOrderAddressFunction.Request, UpdateOrderAddressFunction.Response> {

    private final OrderService orderService;

    @Override
    public Response apply(Request request) {
        // TODO: Implement
        return null;
    }

    public record Request(String orderNumber, String newAddress) {
    }

    public record Response(boolean success, String message) {
    }
}
