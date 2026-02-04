package com.aura.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG Service
 * Retrieval Augmented Generation for product manuals
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RAGService {

    private final SimpleVectorStore vectorStore;
    private final ChatClient chatClient;

    /**
     * Answer question from product manual using RAG
     */
    public String answerFromManual(String question, String productId) {
        log.info("Answering question for product {}: {}", productId, question);

        // Search for relevant documents
        List<Document> relevantDocs = searchSimilar(question, 3);

        if (relevantDocs.isEmpty()) {
            log.warn("No relevant documents found for question: {}", question);
            return "抱歉，我在产品手册中没有找到相关信息。请尝试换个方式提问，或联系客服获取帮助。";
        }

        // Build context from retrieved documents
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantDocs.size(); i++) {
            Document doc = relevantDocs.get(i);
            context.append("信息片段 ").append(i + 1).append(":\n");
            context.append(doc.getText()).append("\n\n");
        }

        // Create prompt for AI
        String prompt = String.format("""
                基于以下产品手册信息回答用户的问题。请用简洁、专业的语气回答。

                产品手册信息：
                %s

                用户问题：%s

                回答（请基于以上信息作答，如果信息不足以回答问题，请诚恳告知用户）：
                """, context.toString(), question);

        // Generate answer using ChatClient
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("Generated answer ({} chars) from {} documents", answer.length(), relevantDocs.size());
        return answer;
    }

    /**
     * Search similar documents using vector similarity
     */
    public List<Document> searchSimilar(String query, int topK) {
        log.debug("Searching for similar documents: query='{}', topK={}", query, topK);

        // Create search request using builder pattern
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.7)
                .build();

        // Execute vector search
        List<Document> results = vectorStore.similaritySearch(searchRequest);

        log.info("Found {} similar documents for query", results.size());
        return results;
    }

    /**
     * Add document to vector store
     */
    public void addDocument(Document document) {
        log.info("Adding document to vector store: id={}", document.getId());
        vectorStore.add(List.of(document));
        log.debug("Document added successfully");
    }
}
