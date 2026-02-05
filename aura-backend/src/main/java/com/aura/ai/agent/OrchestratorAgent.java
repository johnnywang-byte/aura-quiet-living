package com.aura.ai.agent;

import com.aura.service.ProductService;
import com.aura.service.OrderService;
import com.aura.service.ai.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

/**
 * Orchestrator Agent
 * Main coordinator that routes tasks to specialized agents
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrchestratorAgent {

    private final ChatClient chatClient;
    private final ProductExpertAgent productExpertAgent;
    private final CustomerServiceAgent customerServiceAgent;
    private final ProductService productService;
    private final OrderService orderService;
    private final MemoryService memoryService;

    /**
     * Intent classification prompt template
     */
    private static final String INTENT_PROMPT_TEMPLATE = """
            Classify the user's message into one of the following intents:

            1. PRODUCT_INQUIRY: Questions about products, their features, prices, availability, or recommendations
            2. ORDER_SERVICE: Questions about orders, shipping, returns, or customer service
            3. GENERAL_CHAT: General conversation not related to products or orders
            4. UNKNOWN: Cannot be classified into the above categories

            User message: {message}

            Return only the intent name (one of the four options above) without any additional explanation.
            """;

    /**
     * Analyze user intent
     */
    public String analyzeIntent(String message) {
        // Validate input
        if (message == null || message.trim().isEmpty()) {
            log.warn("Empty message provided for intent analysis");
            return "UNKNOWN";
        }

        log.info("Analyzing intent for message: {}", message);

        try {
            // Create prompt template for intent classification
            String promptString = INTENT_PROMPT_TEMPLATE.replace("{message}", message);
            String intent = chatClient.prompt().user(promptString).call().content().trim();

            // Validate intent result
            if (intent == null || intent.isEmpty()) {
                log.warn("Empty intent returned from ChatClient");
                return "UNKNOWN";
            }

            log.info("Classified intent: {}", intent);
            return intent;
        } catch (Exception e) {
            log.error("Failed to analyze intent: {}", e.getMessage(), e);
            return "UNKNOWN";
        }
    }

    /**
     * Route message to appropriate agent based on intent
     */
    public String routeMessage(String message, String sessionId) {
        log.info("Routing message for session: {}", sessionId);

        try {
            String intent = analyzeIntent(message);

            switch (intent) {
                case "PRODUCT_INQUIRY":
                    log.info("Routing to ProductExpertAgent for session: {}", sessionId);
                    return productExpertAgent.handleProductInquiry(message, sessionId);

                case "ORDER_SERVICE":
                    log.info("Routing to CustomerServiceAgent for session: {}", sessionId);
                    // Extract entities from message to find order number
                    var entities = memoryService.extractEntities(message);
                    if (entities.containsKey("orderNumbers")) {
                        java.util.List<String> orderNumbers = (java.util.List<String>) entities.get("orderNumbers");
                        if (!orderNumbers.isEmpty()) {
                            return customerServiceAgent.handleOrderInquiry(orderNumbers.get(0));
                        }
                    }
                    // Default response if no order number found
                    return "I can help with your order-related questions. Please provide your order number.";

                case "GENERAL_CHAT":
                    log.info("Handling general chat for session: {}", sessionId);
                    return "Thank you for chatting! How can I assist you today?";

                default:
                    log.info("Unknown intent, handling directly for session: {}", sessionId);
                    return "I'm sorry, I don't understand your question. Could you rephrase it or ask about products or orders?";
            }
        } catch (Exception e) {
            log.error("Failed to route message: {}", e.getMessage(), e);
            return "I'm sorry, I encountered an error while processing your request. Please try again later.";
        }
    }

    /**
     * Orchestrate complex multi-step tasks
     */
    public String orchestrateTask(String task, String sessionId) {
        log.info("Orchestrating task: {} for session: {}", task, sessionId);

        try {
            // Use AI to break down the task into actionable steps
            String planPrompt = String.format(
                    "Break down the following task into a sequence of simple, actionable steps. " +
                            "Each step should be clear, specific, and focused on a single action.\n\n" +
                            "Task: %s\n\nSteps:",
                    task);

            // Generate task plan using ChatClient
            String plan = chatClient.prompt()
                    .user(planPrompt)
                    .call()
                    .content()
                    .trim();
            log.info("Generated task plan: {}", plan);

            // In a real implementation, we would execute each step and coordinate between
            // agents
            // For demonstration, we'll return the plan
            return "I've created an action plan for your task:\n" + plan;
        } catch (Exception e) {
            log.error("Failed to orchestrate task: {}", e.getMessage(), e);
            return "I'm sorry, I couldn't create a plan for your task. Please try again with a more specific request.";
        }
    }
}
