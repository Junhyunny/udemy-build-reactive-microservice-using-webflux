package com.example.movieservice.domain;

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
public class MovieInfo {

    private String movieInfoId;
    private String name;
    private int year;
    private List<String> cast;
    private LocalDate releaseDate;
}
