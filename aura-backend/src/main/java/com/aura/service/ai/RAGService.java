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
     * Answer question using product manual RAG
     */
    public String answerFromManual(String question, String sessionId) {
        log.info("RAG query from session {}: {}", sessionId, question);

        // Search for relevant documents (increased topK for better recall)
        List<Document> relevantDocs = searchSimilar(question, 8);

        if (relevantDocs.isEmpty()) {
            log.warn("No relevant documents found for question: {}", question);
            // Return empty string to let the calling agent handle the response
            return "";
        }

        // Build context from retrieved documents
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantDocs.size(); i++) {
            Document doc = relevantDocs.get(i);
            context.append("Information Fragment ").append(i + 1).append(":\n");
            context.append(doc.getText()).append("\n\n");

            // Debug: log retrieved content and score
            log.debug("Document {}: score={}, text={}",
                    i + 1,
                    doc.getMetadata().get("distance"),
                    doc.getText().substring(0, Math.min(100, doc.getText().length())));
        }

        // Create prompt for AI
        String prompt = String.format(
                """
                        Based on the following product manual information, answer the user's question. Use a concise and professional tone.

                        Product Manual Information:
                        %s

                        User Question: %s

                        Answer (based on the information above, if insufficient information is available, kindly inform the user):
                        """,
                context.toString(), question);

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
                .similarityThreshold(0.4) // Lowered for diverse semantic matching
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
