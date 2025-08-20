package com.parking.reservation.validation.rules;

import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.repo.ParkingLotReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReservationConflictValidationRule implements ParkingLotValidationRule<ParkingLotReservation> {

    private final ParkingLotReservationRepository reservationRepository;

    @Override
    public Mono<Void> validate(ParkingLotReservation reservation) {
        return reservationRepository
                .isParkingLotNewOrActive(
                        reservation.lotId(), reservation.startTime(), reservation.endTime())
                .flatMap(reserved -> {
                    if (reserved) {
                        return createMonoError("Parking Lot " + reservation.lotId() + " is already reserved");
                    }
                    return Mono.empty();
                });
    }
}
