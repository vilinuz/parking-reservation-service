package com.parking.reservation.validation.rules;

import com.parking.reservation.config.ParkingReservationConfigProperties;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.service.ParkingReservationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UtilisationValidationRule implements ParkingLotValidationRule<ParkingLotReservation> {

    private final ParkingReservationConfigProperties configProperties;
    private final ParkingReservationStore parkingReservationStore;

    @Override
    public Mono<Void> validate(ParkingLotReservation reservation) {
        int maxAllowedUsage = configProperties.getMaxAllowedUsage();

        return parkingReservationStore.countActiveReservations()
                .flatMap(active ->
                        active >= maxAllowedUsage ?
                                createMonoError("Parking utilisation exceeds allowed limit of " + maxAllowedUsage) :
                                Mono.empty());
    }
}
