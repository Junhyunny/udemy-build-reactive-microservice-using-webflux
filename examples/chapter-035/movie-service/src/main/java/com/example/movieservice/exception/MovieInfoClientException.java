package com.example.movieservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class MovieInfoClientException extends RuntimeException {

    private final String message;
    private final HttpStatusCode httpStatus;

    public MovieInfoClientException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
