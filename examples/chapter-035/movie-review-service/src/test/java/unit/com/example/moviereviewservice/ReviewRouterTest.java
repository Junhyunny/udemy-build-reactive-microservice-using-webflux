package com.example.moviereviewservice;

import com.example.moviereviewservice.component.GlobalErrorHandler;
import com.example.moviereviewservice.domain.Review;
import com.example.moviereviewservice.handler.ReviewHandler;
import com.example.moviereviewservice.repository.ReviewReactiveRepository;
import com.example.moviereviewservice.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
class ReviewRouterTest {

    @MockBean
    ReviewReactiveRepository reactiveRepository;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addReview() {

        when(reactiveRepository.save(isA(Review.class)))
                .thenReturn(
                        Mono.just(
                                Review.builder()
                                        .reviewId("reviewId")
                                        .movieInfoId(1L)
                                        .comment("Awesome movie")
                                        .rating(BigDecimal.valueOf(9.0))
                                        .build()
                        )
                );


        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(
                        Review.builder()
                                .reviewId(null)
                                .movieInfoId(1L)
                                .comment("Awesome movie")
                                .rating(BigDecimal.valueOf(9.0))
                                .build()
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertNotNull(result.getReviewId());
                    assertThat(result.getMovieInfoId(), equalTo(1L));
                    assertThat(result.getComment(), equalTo("Awesome movie"));
                    assertThat(result.getRating(), equalTo(BigDecimal.valueOf(9.0)));
                });
    }

    @Test
    void getAllReviews() {

        when(reactiveRepository.findAll())
                .thenReturn(
                        Flux.just(
                                Review.builder()
                                        .reviewId(null)
                                        .movieInfoId(1L)
                                        .comment("Awesome movie")
                                        .rating(BigDecimal.valueOf(9.0))
                                        .build(),
                                Review.builder()
                                        .reviewId(null)
                                        .movieInfoId(1L)
                                        .comment("Fantastic movie")
                                        .rating(BigDecimal.valueOf(9.0))
                                        .build(),
                                Review.builder()
                                        .reviewId("1")
                                        .movieInfoId(2L)
                                        .comment("Excellent movie")
                                        .rating(BigDecimal.valueOf(8.0))
                                        .build()
                        )
                );


        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result.size(), equalTo(3));
                });
    }

    @Test
    void updateReview() {

        when(reactiveRepository.findById("1"))
                .thenReturn(
                        Mono.just(
                                Review.builder()
                                        .reviewId("1")
                                        .movieInfoId(1L)
                                        .comment("Excellent movie")
                                        .rating(BigDecimal.valueOf(8.5))
                                        .build()
                        )
                );

        when(reactiveRepository.save(isA(Review.class)))
                .thenReturn(
                        Mono.just(
                                Review.builder()
                                        .reviewId("1")
                                        .movieInfoId(2L)
                                        .comment("Excellent movie 1")
                                        .rating(BigDecimal.valueOf(9.5))
                                        .build()
                        )
                );


        webTestClient
                .put()
                .uri("/v1/reviews/1")
                .bodyValue(
                        Review.builder()
                                .reviewId("1")
                                .movieInfoId(2L)
                                .comment("Excellent movie 1")
                                .rating(BigDecimal.valueOf(9.5))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result.getComment(), equalTo("Excellent movie 1"));
                    assertThat(result.getRating(), equalTo(BigDecimal.valueOf(9.5)));
                });
    }

    @Test
    void deleteReview() {

        when(reactiveRepository.deleteById("1")).thenReturn(Mono.empty());


        webTestClient
                .delete()
                .uri("/v1/reviews/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void movieInfoIdIsNull_badRequest() {

        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(
                        Review.builder()
                                .reviewId(null)
                                .movieInfoId(null)
                                .comment("Awesome movie")
                                .rating(BigDecimal.valueOf(9.0))
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$").isEqualTo("rating.movieInfoId: must not be null")
        ;
    }

    @Test
    void ratingNegative_badRequest() {

        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(
                        Review.builder()
                                .reviewId(null)
                                .movieInfoId(1L)
                                .comment("Awesome movie")
                                .rating(BigDecimal.valueOf(-9.0))
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$").isEqualTo("rating.negative: please pass a non-negative value")
        ;
    }

    @Test
    void dataIsNotExist_notFound() {

        when(reactiveRepository.findById("1")).thenReturn(Mono.empty());


        webTestClient
                .put()
                .uri("/v1/reviews/1")
                .bodyValue(
                        Review.builder()
                                .reviewId("1")
                                .movieInfoId(2L)
                                .comment("Excellent movie 1")
                                .rating(BigDecimal.valueOf(9.5))
                                .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$").isEqualTo("Review not found fo the given review id: 1")
        ;
    }
}
