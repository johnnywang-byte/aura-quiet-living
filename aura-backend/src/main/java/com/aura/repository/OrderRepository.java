package com.aura.repository;

import com.aura.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Order Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by customer email
     */
    java.util.List<Order> findByCustomerEmail(String email);

    /**
     * Find orders by status
     */
    java.util.List<Order> findByStatus(String status);
}
