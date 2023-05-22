package com.example.movieinfoservice;

import com.example.movieinfoservice.domain.MovieInfo;
import com.example.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieInfoRepository repository;

    @BeforeEach
    void setup() {

        var movieInfoList = List.of(
                MovieInfo.builder()
                        .name("Batman Begins")
                        .year(2005)
                        .cast(List.of("Christian Bale", "Michael Cane"))
                        .releaseDate(LocalDate.parse("2005-06-15"))
                        .build(),
                MovieInfo.builder()
                        .name("The Dark Knight")
                        .year(2008)
                        .cast(List.of("Christian Bale", "Heath Ledger"))
                        .releaseDate(LocalDate.parse("2008-07-18"))
                        .build(),
                MovieInfo.builder()
                        .movieInfoId("ABC")
                        .name("The Dark Knight Rises")
                        .year(2012)
                        .cast(List.of("Christian Bale", "Tom Hardy"))
                        .releaseDate(LocalDate.parse("2012-07-20"))
                        .build()
        );

        repository.saveAll(movieInfoList).blockLast();
    }

    @AfterEach
    void afterEach() {
        repository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins 1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();


        webTestClient.post()
                .uri("/v1/movie-infos")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertNotNull(result.getMovieInfoId());
                    assertThat(result.getName(), equalTo("Batman Begins 1"));
                    assertThat(result.getYear(), equalTo(2005));
                    assertThat(result.getCast(), equalTo(List.of("Christian Bale", "Michael Cane")));
                    assertThat(result.getReleaseDate(), equalTo(LocalDate.parse("2005-06-15")));
                })
        ;
    }

    @Test
    void getAllMovieInfoList() {

        webTestClient.get()
                .uri("/v1/movie-infos")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3)
        ;
    }

    @Test
    void getMovieInfoById() {

        webTestClient.get()
                .uri("/v1/movie-infos/{id}", "ABC")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo("ABC")
                .jsonPath("$.name").isEqualTo("The Dark Knight Rises")
                .jsonPath("$.year").isEqualTo(2012)
                .jsonPath("$.cast[0]").isEqualTo("Christian Bale")
                .jsonPath("$.cast[1]").isEqualTo("Tom Hardy")
                .jsonPath("$.releaseDate").isEqualTo("2012-07-20")
        ;
    }

    @Test
    void updateMovieInfo() {

        webTestClient.put()
                .uri("/v1/movie-info/{id}", "ABC")
                .bodyValue(
                        MovieInfo.builder()
                                .name("The Dark Knight Rises 1")
                                .year(2023)
                                .cast(List.of("Christian Bale", "Tom Hardy"))
                                .releaseDate(LocalDate.parse("2023-07-20"))
                                .build()
                )
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo("ABC")
                .jsonPath("$.name").isEqualTo("The Dark Knight Rises 1")
                .jsonPath("$.year").isEqualTo(2023)
                .jsonPath("$.cast[0]").isEqualTo("Christian Bale")
                .jsonPath("$.cast[1]").isEqualTo("Tom Hardy")
                .jsonPath("$.releaseDate").isEqualTo("2023-07-20")
        ;
    }

    @Test
    void deleteMovieInfo() {

        webTestClient.delete()
                .uri("/v1/movie-info/{id}", "ABC")
                .exchange()
                .expectStatus().isNoContent()
        ;
    }

    @Test
    void getMovieInfoById_notFound() {

        webTestClient.get()
                .uri("/v1/movie-infos/{id}", "ABD")
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

    @Test
    void updateMovieInfo_notFound() {

        webTestClient.put()
                .uri("/v1/movie-info/{id}", "ABD")
                .bodyValue(
                        MovieInfo.builder()
                                .name("The Dark Knight Rises 1")
                                .year(2023)
                                .cast(List.of("Christian Bale", "Tom Hardy"))
                                .releaseDate(LocalDate.parse("2023-07-20"))
                                .build()
                )
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

    @Test
    void withYear_getAllMovieInfoList() {

        webTestClient.get()
                .uri(
                        UriComponentsBuilder
                                .fromUriString("/v1/movie-infos")
                                .queryParam("year", 2008)
                                .toUriString()
                )
                .exchange()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("The Dark Knight")
        ;
    }

    @Test
    void getAllMovieInfoStream() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins 1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();


        webTestClient.post()
                .uri("/v1/movie-infos")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
        ;

        var movieInfoStream = webTestClient
                .get()
                .uri("/v1/movie-infos/stream")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(movieInfoStream)
                .assertNext(responseMovieInfo -> {
                    assertThat(responseMovieInfo.getMovieInfoId() != null, equalTo(true));
                })
                .thenCancel()
                .verify();
    }
}