package com.example.movieinfoservice.controller;

import com.example.movieinfoservice.domain.MovieInfo;
import com.example.movieinfoservice.service.MovieInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;

    private final Sinks.Many<MovieInfo> movieInfoSinks = Sinks.many().replay().all();

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@Valid @RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo)
                .log()
                .doOnNext(movieInfoSinks::tryEmitNext);
        // publish that movie to something
        // subscriber to this movie info
    }

    @GetMapping("/movie-infos")
    public Flux<MovieInfo> getAllMovieInfoList(@RequestParam(value = "year", required = false) Integer year) {
        if (year != null) {
            return movieInfoService.getMovieInfoByYear(year).log();
        }
        return movieInfoService.getAllMovieInfoList().log();
    }

    @GetMapping("/movie-infos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return movieInfoService.getMovieInfoById(id)
                .log()
                .map(info -> ResponseEntity.ok().body(info))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                ;
    }

    @GetMapping(value = "/movie-infos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovieInfoStream() {
        System.out.println("asFlux");
        return movieInfoSinks.asFlux();
    }

    @PutMapping("/movie-infos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo movieInfo) {
        return movieInfoService.updateMovieInfo(id, movieInfo)
                .log()
                .map(info -> ResponseEntity.ok().body(info))
                // .defaultIfEmpty(ResponseEntity.notFound().build())
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                ;
    }

    @DeleteMapping("/movie-infos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
