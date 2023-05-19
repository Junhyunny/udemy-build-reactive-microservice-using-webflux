package com.example.movieservice.client;

import com.example.movieservice.util.RetryUtil;
import com.example.movieservice.domain.MovieInfo;
import com.example.movieservice.exception.MovieInfoClientException;
import com.example.movieservice.exception.MovieInfoServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class MoviesInfoRestClient {

    private final WebClient webClient;
    private final String movieInfoUrl;

    public MoviesInfoRestClient(
            WebClient webClient,
            @Value("${rest-client.movie-info-url}") String movieInfoUrl
    ) {
        this.webClient = webClient;
        this.movieInfoUrl = movieInfoUrl;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {


        return webClient.get()
                .uri(movieInfoUrl.concat("/{id}"), movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(NOT_FOUND)) {
                        return Mono.error(
                                new MovieInfoClientException(
                                        "there is no movie info available for passed in id: " + movieId,
                                        clientResponse.statusCode()
                                )
                        );
                    }
                    return clientResponse
                            .bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new MovieInfoClientException(response, clientResponse.statusCode())));
                })
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> clientResponse
                                .bodyToMono(String.class)
                                .flatMap(response -> Mono.error(new MovieInfoServerException(response, clientResponse.statusCode()))))
                .bodyToMono(MovieInfo.class)
                // .retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log()
                ;
    }
}
