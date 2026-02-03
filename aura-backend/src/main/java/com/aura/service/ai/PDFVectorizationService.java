package com.aura.service.ai;

import com.aura.repository.ProductManualRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * PDF Vectorization Service
 * Processes PDF manuals and stores in vector database
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PDFVectorizationService {

    private final SimpleVectorStore vectorStore;
    private final ProductManualRepository manualRepository;

    @Value("${app.pdf.manuals-path:classpath:manuals/}")
    private String manualsPath;

    @Value("${app.vector.chunk-size:500}")
    private int chunkSize;

    @Value("${app.vector.chunk-overlap:50}")
    private int chunkOverlap;

    /**
     * Initialize vector store from PDFs on startup
     */
    @PostConstruct
    public void initializeVectorStore() {
        try {
            File vectorStoreFile = new File("./data/vector-store.json");

            if (vectorStoreFile.exists()) {
                log.info("Vector store loaded from disk: {}", vectorStoreFile.getAbsolutePath());
                return;
            }

            log.info("Initializing vector store from PDF manuals...");

            // Get manuals directory - handle both classpath and filesystem paths
            String manualsDir = manualsPath.replace("classpath:", "src/main/resources/");
            File directory = new File(manualsDir);

            if (!directory.exists() || !directory.isDirectory()) {
                log.warn("Manuals directory does not exist: {}", directory.getAbsolutePath());
                log.info("Vector store initialized empty (no PDFs to process)");
                return;
            }

            File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (pdfFiles == null || pdfFiles.length == 0) {
                log.warn("No PDF files found in: {}", directory.getAbsolutePath());
                log.info("Vector store initialized empty (no PDFs to process)");
                return;
            }

            int totalDocuments = 0;
            int totalChunks = 0;

            for (File pdfFile : pdfFiles) {
                String productId = pdfFile.getName().replace(".pdf", "");
                try {
                    int chunks = vectorizeProductManual(productId, pdfFile.getAbsolutePath());
                    totalDocuments++;
                    totalChunks += chunks;
                } catch (Exception e) {
                    log.error("Failed to vectorize {}: {}", pdfFile.getName(), e.getMessage());
                }
            }

            // Save vector store to disk
            vectorStoreFile.getParentFile().mkdirs();
            vectorStore.save(vectorStoreFile);

            log.info("✅ Vector store initialized: {} documents with {} chunks", totalDocuments, totalChunks);

        } catch (Exception e) {
            log.error("Error initializing vector store: {}", e.getMessage(), e);
        }
    }

    /**
     * Vectorize a single product manual
     * 
     * @return number of chunks created
     */
    public int vectorizeProductManual(String productId, String pdfPath) {
        log.info("Vectorizing product manual: {} from {}", productId, pdfPath);

        // Validate PDF
        if (!com.aura.util.PDFParser.isValidPDF(pdfPath)) {
            throw new IllegalArgumentException("Invalid PDF file: " + pdfPath);
        }

        // Extract text from PDF
        String rawText = extractTextFromPDF(pdfPath);

        // Clean text
        String cleanedText = com.aura.util.PDFParser.cleanText(rawText);

        if (cleanedText.isEmpty()) {
            log.warn("No text extracted from PDF: {}", pdfPath);
            return 0;
        }

        // Split into chunks
        List<String> chunks = splitIntoChunks(cleanedText, chunkSize);

        // Create documents with metadata
        List<Document> documents = new java.util.ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            java.util.Map<String, Object> metadata = new java.util.HashMap<>();
            metadata.put("product_id", productId);
            metadata.put("source", new java.io.File(pdfPath).getName());
            metadata.put("chunk_index", i);

            Document doc = new Document(chunks.get(i), metadata);
            documents.add(doc);
        }

        // Add to vector store
        vectorStore.add(documents);

        log.info("✅ Vectorized product {} with {} chunks", productId, chunks.size());
        return chunks.size();
    }

    /**
     * Extract text from PDF file using PDFBox
     */
    private String extractTextFromPDF(String pdfPath) {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(new java.io.File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug("Extracted {} characters from {}", text.length(), pdfPath);
            return text;
        } catch (java.io.IOException e) {
            log.error("Failed to extract text from PDF: {}", pdfPath, e);
            throw new RuntimeException("Failed to extract text from PDF: " + pdfPath, e);
        }
    }

    /**
     * Split text into chunks with overlap
     */
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new java.util.ArrayList<>();

        if (text.length() <= chunkSize) {
            chunks.add(text);
            return chunks;
        }

        // Split by sentences (simple approach: split by ". " or ".\n")
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                // Save current chunk
                chunks.add(currentChunk.toString().trim());

                // Start new chunk with overlap (last ~50 chars from previous chunk)
                String overlap = "";
                String prevChunk = currentChunk.toString();
                if (prevChunk.length() > chunkOverlap) {
                    overlap = prevChunk.substring(prevChunk.length() - chunkOverlap);
                }

                currentChunk = new StringBuilder(overlap + " " + sentence);
            } else {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
            }
        }

        // Add final chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        log.debug("Split text into {} chunks (size: {}, overlap: {})", chunks.size(), chunkSize, chunkOverlap);
        return chunks;
    }
}
