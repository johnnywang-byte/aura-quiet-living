package com.aura.repository;

import com.aura.model.entity.ProductManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Manual Repository
 * Provides data access methods for ProductManual entities
 * Optimized for RAG (Retrieval-Augmented Generation) system
 */
@Repository
public interface ProductManualRepository extends JpaRepository<ProductManual, Long> {

    /**
     * Find manuals by product ID
     *
     * @param productId the product ID to search for
     * @return List of product manuals for the given product
     */
    List<ProductManual> findByProductId(String productId);

    /**
     * Find manuals by product ID ordered by chunk index
     *
     * @param productId the product ID to search for
     * @return List of product manuals ordered by chunk index
     */
    List<ProductManual> findByProductIdOrderByChunkIndexAsc(String productId);

    /**
     * Find manuals by product ID and manual version
     *
     * @param productId the product ID to search for
     * @param manualVersion the manual version to search for
     * @return List of product manuals matching both criteria
     */
    List<ProductManual> findByProductIdAndManualVersion(String productId, String manualVersion);

    /**
     * Find manuals by product ID and page number
     *
     * @param productId the product ID to search for
     * @param pageNumber the page number to search for
     * @return List of product manuals for the given product and page
     */
    List<ProductManual> findByProductIdAndPageNumber(String productId, Integer pageNumber);

    /**
     * Delete manuals by product ID
     *
     * @param productId the product ID to delete manuals for
     */
    void deleteByProductId(String productId);

    /**
     * Delete manuals by product ID and manual version
     *
     * @param productId the product ID to delete manuals for
     * @param manualVersion the manual version to delete
     */
    void deleteByProductIdAndManualVersion(String productId, String manualVersion);

    /**
     * Count manuals by product ID
     *
     * @param productId the product ID to count manuals for
     * @return Number of manuals for the given product
     */
    long countByProductId(String productId);
}
