package com.example.moviereviewservice.exception;

public class ReviewNotFoundException extends RuntimeException {

    private final String message;
    private final Throwable throwable;

    public ReviewNotFoundException(String message) {
        this(message, null);
    }

    public ReviewNotFoundException(String message, Throwable throwable) {
        super(message);
        this.message = message;
        this.throwable = throwable;
    }
}
