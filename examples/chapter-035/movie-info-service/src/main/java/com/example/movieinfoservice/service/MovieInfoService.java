package com.example.movieinfoservice.service;

import com.example.movieinfoservice.repository.MovieInfoRepository;
import com.example.movieinfoservice.domain.MovieInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfoList() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo) {
        return movieInfoRepository.findById(id)
                .flatMap(info -> movieInfoRepository.save(
                        MovieInfo.builder()
                                .movieInfoId(info.getMovieInfoId())
                                .name(movieInfo.getName())
                                .cast(movieInfo.getCast())
                                .releaseDate(movieInfo.getReleaseDate())
                                .year(movieInfo.getYear())
                                .build()
                ));
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getMovieInfoByYear(int year) {
        return movieInfoRepository.findByYear(year);
    }
}
