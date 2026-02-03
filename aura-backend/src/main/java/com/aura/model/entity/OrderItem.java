package com.aura.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Order Item Entity
 * Represents a single item in an order
 */
@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEM_ORDER"))
    private Order order;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Calculate total price for this order item
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
