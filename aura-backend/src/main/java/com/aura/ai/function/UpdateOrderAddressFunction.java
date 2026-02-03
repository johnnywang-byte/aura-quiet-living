package com.aura.ai.function;

import com.aura.model.entity.Order;
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
        Order updated = orderService.updateShippingAddress(request.orderNumber(), request.newAddress());
        if (updated == null) {
            return new Response(false, "订单不存在或更新失败");
        }
        return new Response(true, "配送地址已更新");
    }

    public record Request(String orderNumber, String newAddress) {
    }

    public record Response(boolean success, String message) {
    }
}
