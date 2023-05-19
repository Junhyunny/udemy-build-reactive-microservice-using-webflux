package com.example.movieservice.client;

import com.example.movieservice.util.RetryUtil;
import com.example.movieservice.domain.Review;
import com.example.movieservice.exception.ReviewServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Slf4j
public class ReviewsRestClient {

    private final WebClient webClient;
    private final String movieReviewUrl;

    public ReviewsRestClient(
            WebClient webClient,
            @Value("${rest-client.movie-review-url}") String movieReviewUrl
    ) {
        this.webClient = webClient;
        this.movieReviewUrl = movieReviewUrl;
    }

    public Flux<Review> retrieveReview(String movieId) {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(movieReviewUrl)
                .queryParam("movieInfoId", movieId)
                .build();
        return webClient.get()
                .uri(uriComponents.toUri())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response ->
                                    Mono.error(new RuntimeException(response))
                            );
                })
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(response -> Mono.error(new ReviewServerException(response, clientResponse.statusCode())))
                )
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retrySpec())
                .log()
                ;
    }
}
