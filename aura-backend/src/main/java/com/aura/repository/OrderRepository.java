package com.aura.repository;

import com.aura.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 * Provides data access methods for Order entities
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     * @param orderNumber the order number to search for
     * @return Optional containing the found order or empty
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by customer email
     * @param email the customer email to search for
     * @return List of orders for the given email
     */
    List<Order> findByCustomerEmail(String email);

    /**
     * Find orders by status
     *
     * @param status the order status to search for
     * @return List of orders with the given status
     */
    List<Order> findByStatus(String status);

    /**
     * Find orders by customer email and status
     *
     * @param email the customer email to search for
     * @param status the order status to search for
     * @return List of orders matching both criteria
     */
    List<Order> findByCustomerEmailAndStatus(String email, String status);

    /**
     * Count orders by status
     *
     * @param status the order status to count
     * @return Number of orders with the given status
     */
    long countByStatus(String status);
}
