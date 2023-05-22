package com.example.movieservice.controller;

import com.example.movieservice.client.MoviesInfoRestClient;
import com.example.movieservice.client.ReviewsRestClient;
import com.example.movieservice.domain.Movie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;

    public MoviesController(
            MoviesInfoRestClient moviesInfoRestClient,
            ReviewsRestClient reviewsRestClient
    ) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable(name = "id") String movieId) {
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsListMono = reviewsRestClient.retrieveReview(movieId).collectList();
                    return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Movie> retrieveMoviesStream() {
        return moviesInfoRestClient.retrieveMovieInfoStream()
                .map(movieInfo -> new Movie(movieInfo, Collections.emptyList()));
    }
}
