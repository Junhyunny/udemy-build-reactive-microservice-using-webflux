package com.example.movieservice.util;

import com.example.movieservice.exception.MovieInfoServerException;
import com.example.movieservice.exception.ReviewServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetryUtil {

    public static Retry retrySpec() {
        // 특정 exception 통과
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof MovieInfoServerException || throwable instanceof ReviewServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }
}
