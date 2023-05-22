package com.example.moviereviewservice.router;

import com.example.moviereviewservice.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    private final ReviewHandler reviewHandler;

    public ReviewRouter(ReviewHandler reviewHandler) {
        this.reviewHandler = reviewHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> reviewsRouter() {
        return route()
                // 동일한 경로를 묶는 처리
                .nest(
                        path("/v1/reviews"),
                        builder -> builder
                                .POST("", reviewHandler::addReview)
                                .GET("", reviewHandler::getReviews)
                                .PUT("/{id}", reviewHandler::updateReview)
                                .DELETE("/{id}", reviewHandler::deleteReview)
                                .GET("/stream", reviewHandler::getReviewsStream)
                )
                .GET("/v1/hello-world", request -> ServerResponse.ok().bodyValue("Hello World"))
                .build();
    }
}
