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
        try {
            Order order = orderService.createOrder(request);
            return ApiResponse.success(order, "Order created successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * GET /api/orders/{orderNumber} - Get order by number
     */
    @GetMapping("/{orderNumber}")
    public ApiResponse<Order> getOrder(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByNumber(orderNumber);
            if (order == null) {
                return ApiResponse.error("Order not found");
            }
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get order: " + e.getMessage());
        }
    }

    /**
     * GET /api/orders - Get all orders
     */
    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get orders: " + e.getMessage());
        }
    }

    /**
     * PUT /api/orders/{orderNumber}/address - Update shipping address
     */
    @PutMapping("/{orderNumber}/address")
    public ApiResponse<Order> updateAddress(
            @PathVariable String orderNumber,
            @RequestBody String newAddress) {
        try {
            Order order = orderService.updateShippingAddress(orderNumber, newAddress);
            if (order == null) {
                return ApiResponse.error("Order not found");
            }
            return ApiResponse.success(order, "Address updated successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to update address: " + e.getMessage());
        }
    }
}
