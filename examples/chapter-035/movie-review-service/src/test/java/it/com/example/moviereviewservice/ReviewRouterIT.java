package com.example.moviereviewservice;

import com.example.moviereviewservice.domain.Review;
import com.example.moviereviewservice.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewRouterIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewRepository;

    @BeforeEach
    void beforeEach() {

        var reviewList = List.of(
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
        );

        reviewRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void afterEach() {
        reviewRepository.deleteAll().block();
    }

    @Test
    void addReview() {

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

        webTestClient
                .delete()
                .uri("/v1/reviews/1")
                .exchange()
                .expectStatus().isNoContent();


        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectBodyList(Review.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result.size(), equalTo(2));
                });
    }

    @Test
    void getReviewsByMovieInfoId() {

        UriComponents uri = UriComponentsBuilder.fromUriString("/v1/reviews")
                .queryParam("movieInfoId", 1)
                .build();

        webTestClient
                .get()
                .uri(uri.toUri())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result.size(), equalTo(2));
                });
    }
}
