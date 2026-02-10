package com.aura.ai.function;

import com.aura.service.ai.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Query Product Manual Function
 */
@Component
@Description("Query product manual for detailed specifications and instructions")
@RequiredArgsConstructor
public class QueryProductManualFunction
        implements Function<QueryProductManualFunction.Request, QueryProductManualFunction.Response> {

    private final RAGService ragService;

    @Override
    public Response apply(Request request) {

        String answer = ragService.answerFromManual(request.question(), request.productId());
        if (answer == null)
            answer = "There is currently no product manual available.";
        return new Response(answer, "product_manual");
    }

    public record Request(String productId, String question) {
    }

    public record Response(String answer, String source) {
    }
}
