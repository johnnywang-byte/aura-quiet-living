package com.aura.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Product Manual Entity
 * Represents a chunk of product manual content for RAG system
 */
@Entity
@Table(name = "product_manuals", indexes = {
        @Index(name = "IDX_PRODUCT_MANUAL_PRODUCT", columnList = "product_id"),
        @Index(name = "IDX_PRODUCT_MANUAL_CHUNK", columnList = "product_id, chunk_index")
})
@Data
public class ProductManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "chunk_size")
    private Integer chunkSize;

    @Column(name = "manual_version", length = 20)
    private String manualVersion;
}
