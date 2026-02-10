package com.aura.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI Configuration
 * 
 * Configures OpenAI chat client and embedding model for AI agent functionality.
 */
@Configuration
public class OpenAIConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultFunctions(
                        // === CustomerServiceAgent Functions (Function Calling) ===
                        "updateOrderAddressFunction",
                        "getOrderStatusFunction",
                        "getOrdersByEmailFunction",
                        "cancelOrderFunction",
                        "checkInventoryFunction",
                        
                        // === ProductExpertAgent Functions (Direct Service Call) ===
                        // Note: ProductExpertAgent does not use Function Calling.
                        // It directly calls productService and ragService in Java code.
                        // These are registered for potential future use.
                        "queryProductManualFunction",
                        "searchProductsFunction")
                .build();
    }
}
