package com.example.movieinfoservice.controller;

import com.example.movieinfoservice.domain.MovieInfo;
import com.example.movieinfoservice.service.MovieInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@Valid @RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
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
