package edu.manipal.cse.lectureservicereactive.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class ChapterServiceClient {

    private final WebClient webClient;

    public ChapterServiceClient(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://question-service/api/v1").build();
    }

    public Mono<Boolean> chapterExists(UUID chapterId) {
        if (chapterId == null) {
            return Mono.just(false);
        }
        log.debug("Checking existence of chapter ID: {}", chapterId);
        return this.webClient
                .get()
                .uri("/chapters/{id}", chapterId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(WebClientResponseException.class, ex ->
                        ex.getStatusCode() == HttpStatus.NOT_FOUND ? Mono.just(false) : Mono.error(ex)
                )
                .onErrorResume(e -> {
                    log.error("Failed to check chapter existence for ID {}: {}", chapterId, e.getMessage());
                    return Mono.just(false);
                });
    }


}
