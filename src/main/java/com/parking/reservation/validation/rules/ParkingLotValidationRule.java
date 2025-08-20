package com.parking.reservation.validation.rules;

import reactor.core.publisher.Mono;

public interface ParkingLotValidationRule<T> {
    Mono<Void> validate(T target);

    default Mono<Void> createMonoError(String message) {
        return Mono.error(new IllegalArgumentException(message));
    }
}
