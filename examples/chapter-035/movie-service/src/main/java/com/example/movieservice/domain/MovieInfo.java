package com.example.movieservice.domain;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@ToString
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
