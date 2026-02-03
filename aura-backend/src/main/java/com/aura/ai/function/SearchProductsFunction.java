package com.aura.ai.function;

import com.aura.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.List;

/**
 * Search Products Function
 */
@Component
@Description("Search products by keyword or category")
@RequiredArgsConstructor
public class SearchProductsFunction
        implements Function<SearchProductsFunction.Request, SearchProductsFunction.Response> {

    private final ProductService productService;

    @Override
    public Response apply(Request request) {
        // TODO: Implement
        return null;
    }

    public record Request(String keyword, String category) {
    }

    public record Response(List<ProductInfo> products) {
    }

    public record ProductInfo(String id, String name, double price, String category) {
    }
}
