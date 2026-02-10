package com.aura.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Vector Store Configuration
 * 
 * Configures SimpleVectorStore for RAG (Retrieval Augmented Generation).
 */
@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.simple.file-path}")
    private String vectorStoreFilePath;

    @Bean
    public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = new File(vectorStoreFilePath);

        // Create parent directory if it doesn't exist
        vectorStoreFile.getParentFile().mkdirs();

        // Load existing vector store data if file exists
        if (vectorStoreFile.exists()) {
            vectorStore.load(vectorStoreFile);
        }

        return vectorStore;
    }
}
