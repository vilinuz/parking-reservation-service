package com.parking.reservation.api;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class ErrorResponseUtil {
    public static Mono<ServerResponse> errorResponse(int status, String error, String message) {
        ApiError body = new ApiError(status, error, message, Instant.now());
        return ServerResponse
                .status(status)
                .bodyValue(body);
    }

}
