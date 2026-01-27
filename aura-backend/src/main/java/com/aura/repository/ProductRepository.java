package com.aura.repository;

import com.aura.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);

    /**
     * Search products by name containing keyword
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * Find products with stock greater than zero
     */
    List<Product> findByStockGreaterThan(Integer stock);
}
