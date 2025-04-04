package edu.manipal.cse.lectureservicereactive.clients;

import edu.manipal.cse.lectureservicereactive.dto.response.ApiResponse;
import edu.manipal.cse.lectureservicereactive.dto.response.QuestionResponse;
import edu.manipal.cse.lectureservicereactive.exceptions.OperationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuestionServiceClient {

    private final WebClient webClient;

    public QuestionServiceClient(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://question-service/api/v1").build();
    }

    public Mono<ApiResponse<List<QuestionResponse>>> validateAllQuestions(List<UUID> uuids) {
        log.debug("Calling question-service POST /questions/validate for IDs: {}", uuids);

        ParameterizedTypeReference<ApiResponse<List<QuestionResponse>>> responseType =
                new ParameterizedTypeReference<>() {};

        return this.webClient
                .post()
                .uri("/questions/validate")
                .bodyValue(uuids)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("[No Response Body]")
                                .flatMap(errorBody -> {
                                    log.error("HTTP Error response from question-service status: {}, body: {}", clientResponse.statusCode(), errorBody);
                                    var exception = new OperationFailedException(
                                            String.format("Question service call failed with HTTP status %s. Body: %s", clientResponse.statusCode(), errorBody),
                                            WebClientResponseException.create(clientResponse.statusCode().value(), "Error from question-service", null, null, null)
                                    );
                                    return Mono.error(exception);
                                })
                )
                .bodyToMono(responseType)
                .flatMap(apiResponse -> {
                    if (apiResponse.isSuccess()) {
                        log.debug("Successfully validated questions. Response status: {}, Found: {} questions.",
                                apiResponse.getStatus(), apiResponse.getData() != null ? apiResponse.getData().size() : 0);
                        return Mono.just(apiResponse);
                    } else {
                        String errorMessage = buildErrorMessageFromApiResponse(apiResponse);
                        log.warn("Question service indicated failure: {}", errorMessage);
                        return Mono.error(new OperationFailedException(errorMessage));
                    }
                })
                .doOnError(error -> {
                    if (!(error instanceof OperationFailedException)) {
                        log.error("Error during validateAllQuestions call after retrieve/check: {}", error.getMessage(), error);
                    }
                });
    }

    private String buildErrorMessageFromApiResponse(ApiResponse<?> apiResponse) {
        StringBuilder messageBuilder = new StringBuilder("Error from question service: ");
        if (apiResponse.getMessage() != null && !apiResponse.getMessage().isBlank()) {
            messageBuilder.append(apiResponse.getMessage());
        } else {
            messageBuilder.append("Operation failed.");
        }

        if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
            String details = apiResponse.getErrors().stream()
                    .map(err -> err.getField() != null ? String.format("Field '%s': %s", err.getField(), err.getMessage()) : err.getMessage())
                    .collect(Collectors.joining("; "));
            messageBuilder.append(" Details: [").append(details).append("]");
        }
        return messageBuilder.toString();
    }
}