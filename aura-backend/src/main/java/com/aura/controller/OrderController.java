package com.aura.controller;

import com.aura.model.dto.ApiResponse;
import com.aura.model.dto.OrderRequest;
import com.aura.model.entity.Order;
import com.aura.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order Controller
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders - Create order (mock payment)
     */
    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody OrderRequest request) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/orders/{orderNumber} - Get order by number
     */
    @GetMapping("/{orderNumber}")
    public ApiResponse<Order> getOrder(@PathVariable String orderNumber) {
        // TODO: Implement
        return null;
    }

    /**
     * GET /api/orders - Get all orders
     */
    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        // TODO: Implement
        return null;
    }

    /**
     * PUT /api/orders/{orderNumber}/address - Update shipping address
     */
    @PutMapping("/{orderNumber}/address")
    public ApiResponse<Order> updateAddress(
            @PathVariable String orderNumber,
            @RequestBody String newAddress) {
        // TODO: Implement
        return null;
    }
}
