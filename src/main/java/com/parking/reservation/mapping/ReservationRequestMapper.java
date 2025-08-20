package com.parking.reservation.mapping;

import com.parking.reservation.api.model.ReservationRequestDto;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationRequestMapper {
    public ParkingLotReservation mapToEntity(ReservationRequestDto reservation) {
        return new ParkingLotReservation(
                null,
                reservation.lotId(),
                reservation.startTime(),
                reservation.endTime(),
                ReservationStatus.NEW,
                reservation.vehiclePlate());
    }
}
