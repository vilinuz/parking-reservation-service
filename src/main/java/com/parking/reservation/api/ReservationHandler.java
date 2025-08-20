package com.parking.reservation.api;

import com.parking.reservation.api.model.ReservationRequestDto;
import com.parking.reservation.api.model.ReservationResponseDto;
import com.parking.reservation.db.model.ReservationStatus;
import com.parking.reservation.exception.ReservationValidationException;
import com.parking.reservation.mapping.EnumMapper;
import com.parking.reservation.mapping.ReservationRequestMapper;
import com.parking.reservation.mapping.ReservationResponseMapper;
import com.parking.reservation.service.ParkingReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationHandler {

    private final ParkingReservationService service;
    private final ReservationRequestMapper requestMapper;
    private final ReservationResponseMapper responseMapper;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ReservationRequestDto.class)
                .map(requestMapper::mapToEntity)
                .flatMap(service::createReservation)
                .flatMap(_ -> ServerResponse.status(201).build())
                .onErrorResume(ReservationValidationException.class, ex ->
                        errorResponse(400, "Bad Request", ex.getMessage()))
                .onErrorResume(ex ->
                        errorResponse(400, "Bad Request", ex.getMessage())
                );
    }

    // Batch POST
    public Mono<ServerResponse> batchCreate(ServerRequest request) {
        return request.bodyToFlux(ReservationRequestDto.class)
                .map(requestMapper::mapToEntity)
                .flatMap(service::createReservation) // parallel creates, returns Flux<DTO>
                .collectList()
                .flatMap(_ -> ServerResponse.status(201).build())
                .onErrorResume(ReservationValidationException.class, ex ->
                        errorResponse(400, "Bad Request", ex.getMessage()))
                .onErrorResume(ex ->
                        errorResponse(500, "Internal Server Error", ex.getMessage()));
    }

    public Mono<ServerResponse> findAvailableLotNumbers(ServerRequest request) {
        try {
            LocalDateTime start = request.queryParam("startTime")
                    .map(LocalDateTime::parse)
                    .orElse(LocalDateTime.now());
            LocalDateTime end = request.queryParam("endTime")
                    .map(LocalDateTime::parse)
                    .orElseThrow();

            return ServerResponse.ok().bodyValue(service.findAvailableParkingLots(start, end)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .onErrorResume(ex -> errorResponse(404, "Not Found", "Reservation not found")));
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> findReservationsByStatus(ServerRequest request) {
        String reservationStatus = request.queryParam("status").orElse(null);
        if (reservationStatus == null) {
            return errorResponse(400, "Bad Request", "Missing required 'status' parameter");
        }

        try {
            ReservationStatus status = EnumMapper.mapEnum(reservationStatus, ReservationStatus.class);
            return ServerResponse.ok().body(
                    service.findReservationsByStatus(status).map(responseMapper::mapToDto), ReservationResponseDto.class
            );
        } catch (Exception e) {
            return errorResponse(400, "Bad Request", "Invalid status value");
        }
    }

    // Helper for error bodies
    private static Mono<ServerResponse> errorResponse(int status, String error, String message) {
        ApiError apiError = new ApiError(status, error, message, Instant.now());
        return ServerResponse.status(status).bodyValue(apiError);
    }
}
