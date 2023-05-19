package com.example.movieservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    private String movieInfoId;
    private String name;
    private int year;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private List<String> cast;
    private List<Review> reviews;

    public Movie(MovieInfo movieInfo, List<Review> reviews) {
        movieInfoId = movieInfo.getMovieInfoId();
        name = movieInfo.getName();
        year = movieInfo.getYear();
        cast = movieInfo.getCast();
        releaseDate = movieInfo.getReleaseDate();
        this.reviews = reviews;
    }
}
