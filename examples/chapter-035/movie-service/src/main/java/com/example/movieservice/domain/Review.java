package com.example.movieservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private String reviewId;
    private Long movieInfoId;
    private String comment;
    private BigDecimal rating;
}
