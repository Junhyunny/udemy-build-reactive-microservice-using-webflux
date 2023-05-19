package com.example.movieinfoservice;

import com.example.movieinfoservice.domain.MovieInfo;
import com.example.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIT {

    @Autowired
    MovieInfoRepository sut;

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

        sut.saveAll(movieInfoList).blockLast();
    }

    @AfterEach
    void afterEach() {
        sut.deleteAll().block();
    }

    @Test
    void findAll() {

        Flux<MovieInfo> result = sut.findAll().log();

        StepVerifier.create(result)
                .consumeNextWith(movieInfo -> {
                    assertThat(movieInfo.getName(), equalTo("The Dark Knight Rises"));
                    assertThat(movieInfo.getYear(), equalTo(2012));
                    assertThat(movieInfo.getCast().get(0), equalTo("Christian Bale"));
                    assertThat(movieInfo.getCast().get(1), equalTo("Tom Hardy"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2012-07-20")));
                })
                .consumeNextWith(movieInfo -> {
                    assertThat(movieInfo.getName(), equalTo("The Dark Knight"));
                    assertThat(movieInfo.getYear(), equalTo(2008));
                    assertThat(movieInfo.getCast().get(0), equalTo("Christian Bale"));
                    assertThat(movieInfo.getCast().get(1), equalTo("Heath Ledger"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2008-07-18")));
                })
                .consumeNextWith(movieInfo -> {
                    assertThat(movieInfo.getName(), equalTo("Batman Begins"));
                    assertThat(movieInfo.getYear(), equalTo(2005));
                    assertThat(movieInfo.getCast().get(0), equalTo("Christian Bale"));
                    assertThat(movieInfo.getCast().get(1), equalTo("Michael Cane"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2005-06-15")));
                })
                .verifyComplete();
    }

    @Test
    void findById() {

        Mono<MovieInfo> result = sut.findById("ABC").log();

        StepVerifier.create(result)
                .assertNext(movieInfo -> {
                    assertThat(movieInfo.getMovieInfoId(), equalTo("ABC"));
                    assertThat(movieInfo.getName(), equalTo("The Dark Knight Rises"));
                    assertThat(movieInfo.getYear(), equalTo(2012));
                    assertThat(movieInfo.getCast().get(0), equalTo("Christian Bale"));
                    assertThat(movieInfo.getCast().get(1), equalTo("Tom Hardy"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2012-07-20")));
                })
                .verifyComplete();
    }

    @Test
    void save() {

        var newMovieInfo = MovieInfo.builder()
                .movieInfoId("DEF")
                .name("Batman Ends")
                .year(2023)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2023-06-15"))
                .build();


        var result = sut.save(newMovieInfo).log();


        StepVerifier.create(result)
                .assertNext(movieInfo -> {
                    assertThat(movieInfo.getMovieInfoId(), equalTo("DEF"));
                    assertThat(movieInfo.getName(), equalTo("Batman Ends"));
                    assertThat(movieInfo.getYear(), equalTo(2023));
                    assertThat(movieInfo.getCast().get(0), equalTo("Christian Bale"));
                    assertThat(movieInfo.getCast().get(1), equalTo("Michael Cane"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2023-06-15")));
                })
                .verifyComplete();
    }

    @Test
    void update() {

        var movieInfo = sut.findById("ABC").block();


        var result = sut.save(
                MovieInfo.builder()
                        .movieInfoId(movieInfo.getMovieInfoId())
                        .name(movieInfo.getName())
                        .year(2033)
                        .cast(movieInfo.getCast())
                        .releaseDate(LocalDate.parse("2033-06-15"))
                        .build()
        ).log();


        StepVerifier.create(result)
                .assertNext(nextMovieInfo -> {
                    assertThat(nextMovieInfo.getYear(), equalTo(2033));
                    assertThat(nextMovieInfo.getReleaseDate(), equalTo(LocalDate.parse("2033-06-15")));
                })
                .verifyComplete();
    }

    @Test
    void delete() {

        sut.deleteById("ABC").log().block();

        var result = sut.findAll().log();

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByYear() {

        var result = sut.findByYear(2008).log();

        StepVerifier.create(result)
                .consumeNextWith(movieInfo -> {
                    assertThat(movieInfo.getName(), equalTo("The Dark Knight"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2008-07-18")));
                })
                .verifyComplete();
    }

    @Test
    void findOneByName() {

        var result = sut.findOneByName("The Dark Knight").log();

        StepVerifier.create(result)
                .consumeNextWith(movieInfo -> {
                    assertThat(movieInfo.getName(), equalTo("The Dark Knight"));
                    assertThat(movieInfo.getReleaseDate(), equalTo(LocalDate.parse("2008-07-18")));
                })
                .verifyComplete();
    }
}