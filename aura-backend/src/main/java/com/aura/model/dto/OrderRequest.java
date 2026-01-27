package com.aura.model.dto;

import lombok.Data;
import java.util.List;

/**
 * Order Request DTO
 */
@Data
public class OrderRequest {

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String shippingAddress;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private String productId;
        private Integer quantity;
    }
}
