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
        try {
            List<Product> products = productService.getAllProducts();
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get products: " + e.getMessage());
        }
    }

    /**
     * GET /api/products/{id} - Get product by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(@PathVariable String id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ApiResponse.error("Product not found");
            }
            return ApiResponse.success(product);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get product: " + e.getMessage());
        }
    }

    /**
     * GET /api/products/category/{category} - Get products by category
     */
    @GetMapping("/category/{category}")
    public ApiResponse<List<Product>> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get products by category: " + e.getMessage());
        }
    }

    /**
     * GET /api/products/search?q={keyword} - Search products
     */
    @GetMapping("/search")
    public ApiResponse<List<Product>> searchProducts(@RequestParam String q) {
        try {
            List<Product> products = productService.searchProducts(q);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error("Failed to search products: " + e.getMessage());
        }
    }
}
