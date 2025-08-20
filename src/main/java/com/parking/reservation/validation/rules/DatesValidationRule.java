package com.parking.reservation.validation.rules;

import com.parking.reservation.db.model.ParkingLotReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DatesValidationRule implements ParkingLotValidationRule<ParkingLotReservation> {

    @Override
    public Mono<Void> validate(ParkingLotReservation reservation) {
        if (reservation.startTime().isAfter(reservation.endTime()) ||
                reservation.endTime().isBefore(LocalDateTime.now()) ||
                reservation.startTime().isBefore(LocalDateTime.now())) {
            return createMonoError("Reservation StartTime is either after EndTime or StartTime/EndTime is in the past");
        }

        return Mono.empty();
    }
}
