package com.example.movieinfoservice;

import com.example.movieinfoservice.controller.MoviesInfoController;
import com.example.movieinfoservice.domain.MovieInfo;
import com.example.movieinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoService;

    @Test
    void addMovieInfo() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins 1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();

        when(movieInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(
                Mono.just(
                        MovieInfo.builder()
                                .movieInfoId("movieInfoId")
                                .name("Batman Begins 1")
                                .year(2005)
                                .cast(List.of("Christian Bale", "Michael Cane"))
                                .releaseDate(LocalDate.parse("2005-06-15"))
                                .build()
                )
        );

        webTestClient.post()
                .uri("/v1/movie-info")
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
        when(movieInfoService.getAllMovieInfoList()).thenReturn(
                Flux.fromIterable(movieInfoList)
        );


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

        when(movieInfoService.getMovieInfoById("ABC")).thenReturn(
                Mono.just(
                        MovieInfo.builder()
                                .movieInfoId("ABC")
                                .name("The Dark Knight Rises")
                                .year(2012)
                                .cast(List.of("Christian Bale", "Tom Hardy"))
                                .releaseDate(LocalDate.parse("2012-07-20"))
                                .build()
                )
        );


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

        when(movieInfoService.updateMovieInfo(eq("ABC"), isA(MovieInfo.class))).thenReturn(
                Mono.just(
                        MovieInfo.builder()
                                .movieInfoId("ABC")
                                .name("The Dark Knight Rises 1")
                                .year(2023)
                                .cast(List.of("Christian Bale", "Tom Hardy"))
                                .releaseDate(LocalDate.parse("2023-07-20"))
                                .build()
                )
        );


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

        when(movieInfoService.deleteMovieInfo("ABC")).thenReturn(
                Mono.empty()
        );


        webTestClient.delete()
                .uri("/v1/movie-info/{id}", "ABC")
                .exchange()
                .expectStatus().isNoContent()
        ;
    }

    @Test
    void withoutName_addMovieInfo_badRequest() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();


        webTestClient.post()
                .uri("/v1/movie-info")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result, equalTo("movieInfo.name must be present"));
                })
        ;
    }

    @Test
    void negativeYear_addMovieInfo_badRequest() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins")
                .year(-2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();


        webTestClient.post()
                .uri("/v1/movie-info")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result, equalTo("movieInfo.year must be positive value"));
                })
        ;
    }

    @Test
    void emptyCast_addMovieInfo_badRequest() {

        var movieInfo = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins")
                .year(2005)
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();


        webTestClient.post()
                .uri("/v1/movie-info")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    var result = response.getResponseBody();
                    assertThat(result, equalTo("movieInfo.cast must be present"));
                })
        ;
    }
}
