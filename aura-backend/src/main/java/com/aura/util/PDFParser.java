package com.aura.util;

import lombok.extern.slf4j.Slf4j;

/**
 * PDF Parser Utility
 * Helper methods for PDF processing
 */
@Slf4j
public class PDFParser {

    /**
     * Clean extracted text from PDF
     * Removes excessive whitespace, normalizes line breaks, and trims text
     */
    public static String cleanText(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "";
        }

        // Remove excessive whitespace while preserving single spaces
        String cleaned = rawText.replaceAll("\\s+", " ");

        // Normalize line breaks (convert multiple newlines to double newline for
        // paragraphs)
        cleaned = cleaned.replaceAll("\\n{3,}", "\n\n");

        // Remove special characters that might interfere with embeddings
        cleaned = cleaned.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");

        // Trim leading and trailing whitespace
        cleaned = cleaned.trim();

        log.debug("Cleaned text: {} chars -> {} chars", rawText.length(), cleaned.length());
        return cleaned;
    }

    /**
     * Extract metadata from PDF document
     * Returns title, author, creation date, and keywords
     */
    public static java.util.Map<String, String> extractMetadata(org.apache.pdfbox.pdmodel.PDDocument document) {
        java.util.Map<String, String> metadata = new java.util.HashMap<>();

        if (document == null) {
            return metadata;
        }

        try {
            org.apache.pdfbox.pdmodel.PDDocumentInformation info = document.getDocumentInformation();

            if (info != null) {
                addIfNotNull(metadata, "title", info.getTitle());
                addIfNotNull(metadata, "author", info.getAuthor());
                addIfNotNull(metadata, "subject", info.getSubject());
                addIfNotNull(metadata, "keywords", info.getKeywords());

                if (info.getCreationDate() != null) {
                    metadata.put("creation_date", info.getCreationDate().toString());
                }
            }

            metadata.put("pages", String.valueOf(document.getNumberOfPages()));
            log.debug("Extracted metadata: {}", metadata);

        } catch (Exception e) {
            log.warn("Error extracting PDF metadata: {}", e.getMessage());
        }

        return metadata;
    }

    /**
     * Helper method to add non-null metadata values
     */
    private static void addIfNotNull(java.util.Map<String, String> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value.trim());
        }
    }

    /**
     * Validate PDF file exists and can be opened
     */
    public static boolean isValidPDF(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            log.warn("PDF file path is null or empty");
            return false;
        }

        java.io.File file = new java.io.File(filePath);

        // Check if file exists
        if (!file.exists()) {
            log.warn("PDF file does not exist: {}", filePath);
            return false;
        }

        // Check file extension
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            log.warn("File is not a PDF: {}", filePath);
            return false;
        }

        // Try to open the PDF to validate format
        try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {

            if (document.getNumberOfPages() == 0) {
                log.warn("PDF file has no pages: {}", filePath);
                return false;
            }

            log.debug("Valid PDF: {} ({} pages)", filePath, document.getNumberOfPages());
            return true;

        } catch (Exception e) {
            log.error("Invalid PDF file {}: {}", filePath, e.getMessage());
            return false;
        }
    }
}
