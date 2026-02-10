package com.aura.ai.function;

import com.aura.model.entity.Product;
import com.aura.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SearchProductsFunction
        implements Function<SearchProductsFunction.Request, SearchProductsFunction.Response> {

    private final ProductService productService;

    @Override
    public Response apply(Request request) {
        List<Product> list;
        if (request.keyword() != null && !request.keyword().isBlank()) {
            list = productService.searchProducts(request.keyword());
        } else if (request.category() != null && !request.category().isBlank()) {
            list = productService.getProductsByCategory(request.category());
        } else {
            list = productService.getAllProducts();
        }
        if (list == null)
            list = List.of();
        List<ProductInfo> infos = list.stream()
                .map(p -> new ProductInfo(
                        p.getId(),
                        p.getName(),
                        p.getPrice().doubleValue(),
                        p.getCategory()))
                .toList();
        return new Response(infos);
    }

    public record Request(String keyword, String category) {
    }

    public record Response(List<ProductInfo> products) {
    }

    public record ProductInfo(String id, String name, double price, String category) {
    }
}
