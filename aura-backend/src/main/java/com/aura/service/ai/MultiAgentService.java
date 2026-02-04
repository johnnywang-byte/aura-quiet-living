package com.aura.service.ai;

import com.aura.ai.agent.OrchestratorAgent;
import com.aura.ai.agent.ProductExpertAgent;
import com.aura.ai.agent.CustomerServiceAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Multi-Agent Service
 * Orchestrates multiple AI agents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MultiAgentService {

    private final OrchestratorAgent orchestratorAgent;
    private final ProductExpertAgent productExpertAgent;
    private final CustomerServiceAgent customerServiceAgent;

    /**
     * Route message to appropriate agent based on intent
     */
    public String routeToAgent(String message, String sessionId) {
        log.info("Routing message for session {}: {}", sessionId, message);

        // Use orchestrator to analyze intent
        String intent = orchestratorAgent.analyzeIntent(message);

        // Route based on intent (orchestrator returns agent type or handles directly)
        String response;
        if (intent != null && intent.toLowerCase().contains("product")) {
            log.info("Routing to ProductExpertAgent for session {}", sessionId);
            // For now, return placeholder until ProductExpertAgent is implemented
            response = "Product expert would handle: " + message;
        } else if (intent != null
                && (intent.toLowerCase().contains("order") || intent.toLowerCase().contains("service"))) {
            log.info("Routing to CustomerServiceAgent for session {}", sessionId);
            // For now, return placeholder until CustomerServiceAgent is implemented
            response = "Customer service would handle: " + message;
        } else {
            log.info("Handling directly by orchestrator for session {}", sessionId);
            // General chat handled by orchestrator
            response = intent != null ? intent : "I'm here to help! You can ask about products or orders.";
        }

        log.debug("Response generated for session {}: {}", sessionId, response);
        return response;
    }

    /**
     * Coordinate multiple agents for complex tasks
     */
    public String coordinateAgents(String task, String sessionId) {
        log.info("Coordinating agents for complex task in session {}: {}", sessionId, task);

        // Use orchestrator for multi-step coordination
        String result = orchestratorAgent.orchestrateTask(task, sessionId);

        // For now, orchestrator handles the coordination
        // Future: implement multi-step agent workflows
        log.info("Task coordination completed for session {}", sessionId);
        return result != null ? result : "Task coordination in progress...";
    }
}
