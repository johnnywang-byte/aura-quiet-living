package com.aura.repository;

import com.aura.model.entity.ProductManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Manual Repository
 */
@Repository
public interface ProductManualRepository extends JpaRepository<ProductManual, Long> {

    /**
     * Find manuals by product ID
     */
    List<ProductManual> findByProductId(String productId);

    /**
     * Find manuals by product ID ordered by chunk index
     */
    List<ProductManual> findByProductIdOrderByChunkIndexAsc(String productId);

    /**
     * Delete manuals by product ID
     */
    void deleteByProductId(String productId);
}
