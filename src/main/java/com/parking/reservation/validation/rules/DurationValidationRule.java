package com.parking.reservation.validation.rules;

import com.parking.reservation.config.ParkingReservationConfigProperties;
import com.parking.reservation.db.model.ParkingLotReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DurationValidationRule implements ParkingLotValidationRule<ParkingLotReservation> {

    private final ParkingReservationConfigProperties configProperties;

    @Override
    public Mono<Void> validate(ParkingLotReservation reservation) {
        int maxAllowedDuration = configProperties.getMaxBookingDuration();
        Duration duration = Duration.between(reservation.startTime(), reservation.endTime());

        if (duration.isNegative() || duration.isZero() || duration.getSeconds() > maxAllowedDuration) {
            return createMonoError("Reservation Start Time is in the past or max allowed duration exceeded");
        }

        return Mono.empty();
    }
}
