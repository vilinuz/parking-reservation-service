package com.parking.reservation.validation;

import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.validation.rules.DatesValidationRule;
import com.parking.reservation.validation.rules.DurationValidationRule;
import com.parking.reservation.validation.rules.ReservationConflictValidationRule;
import com.parking.reservation.validation.rules.UtilisationValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ParkingReservationValidator {

    private final DatesValidationRule datesValidationRule;
    private final DurationValidationRule durationValidationRule;
    private final ReservationConflictValidationRule reservationConflictValidationRule;
    private final UtilisationValidationRule utilisationValidationRule;

    public Mono<Void> validateAll(ParkingLotReservation reservation) {
        return Flux.fromStream(Stream.of(datesValidationRule, durationValidationRule,
                        reservationConflictValidationRule, utilisationValidationRule))
                .concatMap(rule -> rule.validate(reservation)).next().then();
    }
}
