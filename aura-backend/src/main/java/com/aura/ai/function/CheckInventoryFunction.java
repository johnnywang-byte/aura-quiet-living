package com.aura.ai.function;

import com.aura.model.entity.Product;
import com.aura.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Check Inventory Function
 */
@Component
@Description("Check product inventory availability")
@RequiredArgsConstructor
public class CheckInventoryFunction
        implements Function<CheckInventoryFunction.Request, CheckInventoryFunction.Response> {

    private final ProductService productService;

    @Override
    public Response apply(Request request) {
        Product product = productService.getProductById(request.productId());
        if (product == null) {
            return new Response(request.productId(), 0, false);
        }
        int stock = product.getStock() != null ? product.getStock() : 0;
        return new Response(request.productId(), stock, stock > 0);

    }

    public record Request(String productId) {
    }

    public record Response(String productId, int stock, boolean available) {
    }
}
