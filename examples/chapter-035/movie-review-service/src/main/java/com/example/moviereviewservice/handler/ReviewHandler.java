package com.example.moviereviewservice.handler;

import com.example.moviereviewservice.domain.Review;
import com.example.moviereviewservice.exception.ReviewDataException;
import com.example.moviereviewservice.exception.ReviewNotFoundException;
import com.example.moviereviewservice.repository.ReviewReactiveRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewHandler {

    private final Validator validator;
    private final ReviewReactiveRepository reviewRepository;
    private final Sinks.Many<Review> reviewSinks = Sinks.many().replay().all();

    public ReviewHandler(Validator validator, ReviewReactiveRepository reviewRepository) {
        this.validator = validator;
        this.reviewRepository = reviewRepository;
    }

    private void validate(Review review) {
        var constraintValidations = validator.validate(review);
        log.info("result of validation: {}", constraintValidations);
        if (constraintValidations.size() > 0) {
            var errorMessage = constraintValidations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .doOnNext(reviewSinks::tryEmitNext)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
                ;
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var movieInfoIdOption = serverRequest.queryParam("movieInfoId");
        if (movieInfoIdOption.isPresent()) {
            var movieInfoId = movieInfoIdOption.get();
            return ServerResponse.ok().body(
                    reviewRepository.findByMovieInfoId(Long.parseLong(movieInfoId)), Review.class
            );
        }
        return ServerResponse.ok().body(
                reviewRepository.findAll(), Review.class
        );
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        return reviewRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found fo the given review id: " + reviewId)))
                .flatMap(review ->
                        serverRequest.bodyToMono(Review.class)
                                .flatMap(requestReview -> reviewRepository.save(
                                                Review.builder()
                                                        .reviewId(review.getReviewId())
                                                        .comment(requestReview.getComment())
                                                        .rating(requestReview.getRating())
                                                        .build()
                                        )
                                )
                )
                .flatMap(ServerResponse.ok()::bodyValue)
                // .switchIfEmpty(ServerResponse.notFound().build())
                ;
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        return reviewRepository.deleteById(reviewId)
                .then(ServerResponse.noContent().build())
                ;
    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSinks.asFlux(), Review.class)
                .log();
    }
}
