package com.parking.reservation.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReservationRouter {

    @Bean
    public RouterFunction<ServerResponse> reservationRoutes(ReservationHandler handler) {
        return RouterFunctions
                .route()
                .POST("/reservations", handler::create)
                .POST("/reservations/batch", handler::batchCreate)
                .GET("/reservations", handler::findReservationsByStatus)
                .GET("/parking/reservations/lots/available", handler::findAvailableLotNumbers)
                .build();
    }
}
