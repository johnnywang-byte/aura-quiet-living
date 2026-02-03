package com.aura.controller;

import com.aura.model.dto.ApiResponse;
import com.aura.model.entity.Product;
import com.aura.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Get all products
     */
    @GetMapping
    public ApiResponse<List<Product>> getAllProducts() {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/products/{id} - Get product by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(@PathVariable String id) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/products/category/{category} - Get products by category
     */
    @GetMapping("/category/{category}")
    public ApiResponse<List<Product>> getProductsByCategory(@PathVariable String category) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/products/search?q={keyword} - Search products
     */
    @GetMapping("/search")
    public ApiResponse<List<Product>> searchProducts(@RequestParam String q) {
        // TODO: Implement
        return null;
    }
}
