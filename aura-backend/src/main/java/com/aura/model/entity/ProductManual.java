package com.aura.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Product Manual Entity
 */
@Entity
@Table(name = "product_manuals")
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
}
