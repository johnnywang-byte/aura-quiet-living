package com.aura.controller;

import com.aura.service.ai.PDFVectorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Vector Store Management Controller
 * 向量数据库管理控制器
 */
@RestController
@RequestMapping("/api/admin/vector-store")
@RequiredArgsConstructor
@Slf4j
public class VectorStoreController {

    private final SimpleVectorStore vectorStore;
    private final PDFVectorizationService pdfVectorizationService;
    private final ResourceLoader resourceLoader;

    @Value("${spring.ai.vectorstore.simple.file-path}")
    private String vectorStoreFilePath;

    @Value("${app.pdf.manuals-path:classpath:manuals/}")
    private String manualsPath;

    @Value("${app.vector.chunk-size:500}")
    private int chunkSize;

    @Value("${app.vector.chunk-overlap:50}")
    private int chunkOverlap;

    /**
     * 重新生成向量数据库
     * POST /api/admin/vector-store/rebuild
     */
    @PostMapping("/rebuild")
    public ResponseEntity<Map<String, Object>> rebuildVectorStore() {
        log.info("🔄 Starting vector store rebuild...");
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 删除旧的向量数据库文件
            File vectorStoreFile = new File(vectorStoreFilePath);
            if (vectorStoreFile.exists()) {
                boolean deleted = vectorStoreFile.delete();
                log.info("Deleted old vector store file: {}", deleted);
                response.put("old_file_deleted", deleted);
            }

            // 2. 获取PDF文件目录
            File directory;
            try {
                directory = resourceLoader.getResource(manualsPath).getFile();
            } catch (Exception e) {
                log.error("Failed to resolve manuals path: {}", e.getMessage());
                response.put("error", "无法找到PDF手册目录");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            if (!directory.exists() || !directory.isDirectory()) {
                log.warn("Manuals directory does not exist: {}", directory.getAbsolutePath());
                response.put("error", "PDF手册目录不存在");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            // 3. 获取所有PDF文件
            File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (pdfFiles == null || pdfFiles.length == 0) {
                log.warn("No PDF files found in: {}", directory.getAbsolutePath());
                response.put("error", "未找到PDF文件");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            // 4. 向量化所有PDF
            int totalDocuments = 0;
            int totalChunks = 0;
            Map<String, Integer> documentsProcessed = new HashMap<>();

            for (File pdfFile : pdfFiles) {
                String productId = pdfFile.getName().replace(".pdf", "");
                try {
                    log.info("Processing PDF: {}", pdfFile.getName());
                    int chunks = pdfVectorizationService.vectorizeProductManual(productId, pdfFile.getAbsolutePath());
                    totalDocuments++;
                    totalChunks += chunks;
                    documentsProcessed.put(productId, chunks);
                    log.info("✅ Vectorized {} with {} chunks", productId, chunks);
                } catch (Exception e) {
                    log.error("Failed to vectorize {}: {}", pdfFile.getName(), e.getMessage());
                    documentsProcessed.put(productId + "_ERROR", 0);
                }
            }

            // 5. 保存向量数据库到磁盘
            vectorStoreFile.getParentFile().mkdirs();
            vectorStore.save(vectorStoreFile);
            log.info("Vector store saved to: {}", vectorStoreFile.getAbsolutePath());

            // 6. 构建响应
            response.put("success", true);
            response.put("total_documents", totalDocuments);
            response.put("total_chunks", totalChunks);
            response.put("documents_processed", documentsProcessed);
            response.put("vector_store_path", vectorStoreFile.getAbsolutePath());
            response.put("chunk_size", chunkSize);
            response.put("chunk_overlap", chunkOverlap);
            response.put("message", String.format("✅ 成功重新生成向量数据库：%d个文档，%d个分块", 
                    totalDocuments, totalChunks));

            log.info("✅ Vector store rebuild completed: {} documents, {} chunks", 
                    totalDocuments, totalChunks);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error rebuilding vector store: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取向量数据库状态
     * GET /api/admin/vector-store/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getVectorStoreStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            File vectorStoreFile = new File(vectorStoreFilePath);
            
            status.put("file_exists", vectorStoreFile.exists());
            status.put("file_path", vectorStoreFile.getAbsolutePath());
            
            if (vectorStoreFile.exists()) {
                status.put("file_size_kb", vectorStoreFile.length() / 1024);
                status.put("last_modified", new java.util.Date(vectorStoreFile.lastModified()));
            }
            
            status.put("manuals_path", manualsPath);
            status.put("chunk_size", chunkSize);
            status.put("chunk_overlap", chunkOverlap);
            
            // 检查PDF文件
            try {
                File directory = resourceLoader.getResource(manualsPath).getFile();
                if (directory.exists() && directory.isDirectory()) {
                    File[] pdfFiles = directory.listFiles((dir, name) -> 
                            name.toLowerCase().endsWith(".pdf"));
                    status.put("pdf_files_count", pdfFiles != null ? pdfFiles.length : 0);
                    if (pdfFiles != null && pdfFiles.length > 0) {
                        String[] pdfNames = new String[pdfFiles.length];
                        for (int i = 0; i < pdfFiles.length; i++) {
                            pdfNames[i] = pdfFiles[i].getName();
                        }
                        status.put("pdf_files", pdfNames);
                    }
                }
            } catch (Exception e) {
                status.put("pdf_files_count", 0);
                status.put("pdf_error", e.getMessage());
            }

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error getting vector store status: {}", e.getMessage(), e);
            status.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(status);
        }
    }

    /**
     * 删除向量数据库
     * DELETE /api/admin/vector-store
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteVectorStore() {
        Map<String, Object> response = new HashMap<>();

        try {
            File vectorStoreFile = new File(vectorStoreFilePath);
            
            if (vectorStoreFile.exists()) {
                boolean deleted = vectorStoreFile.delete();
                response.put("deleted", deleted);
                response.put("message", deleted ? "向量数据库已删除" : "删除失败");
                log.info("Vector store deletion: {}", deleted);
            } else {
                response.put("deleted", false);
                response.put("message", "向量数据库文件不存在");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting vector store: {}", e.getMessage(), e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
