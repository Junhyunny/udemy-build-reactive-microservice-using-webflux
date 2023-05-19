package com.example.movieservice;

import com.example.movieservice.domain.MovieInfo;
import com.example.movieservice.domain.Review;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
        properties = {
                "rest-client.movie-info-url= http://localhost:${wiremock.server.port}/v1/movie-infos",
                "rest-client.movie-review-url= http://localhost:${wiremock.server.port}/v1/reviews"
        }
)
class MoviesControllerIT {

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        WireMock.reset();
    }

    @Test
    void retrieveMovieById() throws JsonProcessingException {

        String stubMovieInfo = objectMapper.writeValueAsString(
                MovieInfo.builder()
                        .movieInfoId("1")
                        .name("Hello World")
                        .year(2023)
                        .cast(List.of("Hello", "World"))
                        .releaseDate(LocalDate.parse("2023-01-01"))
                        .build()
        );
        String stubReviews = objectMapper.writeValueAsString(
                List.of(
                        Review.builder()
                                .reviewId("ABC")
                                .comment("This is comment")
                                .rating(BigDecimal.TEN)
                                .movieInfoId(1L)
                                .build()
                )
        );
        stubFor(
                get(urlEqualTo("/v1/movie-infos/1"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(stubMovieInfo)
                        )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .withQueryParam("movieInfoId", WireMock.equalTo("1"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(stubReviews)
                        )
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", 1)
                .exchange()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("Hello World")
                .jsonPath("$.year").isEqualTo(2023)
                .jsonPath("$.cast[0]").isEqualTo("Hello")
                .jsonPath("$.cast[1]").isEqualTo("World")
                .jsonPath("$.releaseDate").isEqualTo("2023-01-01")
                .jsonPath("$.reviews[0].reviewId").isEqualTo("ABC")
                .jsonPath("$.reviews[0].movieInfoId").isEqualTo(1L)
                .jsonPath("$.reviews[0].comment").isEqualTo("This is comment")
                .jsonPath("$.reviews[0].rating").isEqualTo(BigDecimal.TEN)
        ;
    }

    @Test
    void movieInfoNotFound_throwNotFoundException() {

        stubFor(
                get(urlEqualTo("/v1/movie-infos/1"))
                        .willReturn(aResponse().withStatus(404))
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("there is no movie info available for passed in id: 1")
        ;
        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movie-infos/1")));
    }

    @Test
    void reviewsNotFound_throwNotFoundException() throws JsonProcessingException {

        String stubMovieInfo = objectMapper.writeValueAsString(
                MovieInfo.builder()
                        .movieInfoId("1")
                        .name("Hello World")
                        .year(2023)
                        .cast(List.of("Hello", "World"))
                        .releaseDate(LocalDate.parse("2023-01-01"))
                        .build()
        );
        stubFor(
                get(urlEqualTo("/v1/movie-infos/1"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(stubMovieInfo)
                        )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(
                                aResponse().withStatus(404)
                        )
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("Hello World")
                .jsonPath("$.year").isEqualTo(2023)
                .jsonPath("$.cast[0]").isEqualTo("Hello")
                .jsonPath("$.cast[1]").isEqualTo("World")
                .jsonPath("$.releaseDate").isEqualTo("2023-01-01")
                .jsonPath("$.reviews").isEmpty()
        ;
    }

    @Test
    void movieInfoInternalServerError() {

        stubFor(
                get(urlEqualTo("/v1/movie-infos/1"))
                        .willReturn(
                                aResponse().withStatus(500)
                                        .withBody("This is movie info service error")
                        )
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", 1)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("This is movie info service error")
        ;
        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movie-infos/1")));
    }

    @Test
    void reviewsInternalServerError() throws JsonProcessingException {

        String stubMovieInfo = objectMapper.writeValueAsString(
                MovieInfo.builder()
                        .movieInfoId("1")
                        .name("Hello World")
                        .year(2023)
                        .cast(List.of("Hello", "World"))
                        .releaseDate(LocalDate.parse("2023-01-01"))
                        .build()
        );
        stubFor(
                get(urlEqualTo("/v1/movie-infos/1"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(stubMovieInfo)
                        )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(
                                aResponse().withStatus(500)
                                        .withBody("This is reviews service error")
                        )
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", 1)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("This is reviews service error")
        ;
        WireMock.verify(4,
                getRequestedFor(urlPathEqualTo("/v1/reviews"))
                        .withQueryParam("movieInfoId", WireMock.equalTo("1"))
        );
    }
}