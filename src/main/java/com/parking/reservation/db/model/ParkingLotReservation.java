package com.parking.reservation.db.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("parking_lot_reservation")
public record ParkingLotReservation(@Id UUID id,
                                    String lotId,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime,
                                    ReservationStatus status,
                                    String vehiclePlate) {

    public ParkingLotReservation activate() {
        return new ParkingLotReservation(id, lotId, startTime, endTime, ReservationStatus.ACTIVE, vehiclePlate);
    }

    public ParkingLotReservation expire() {
        return new ParkingLotReservation(id, lotId, startTime, endTime, ReservationStatus.COMPLETED, vehiclePlate);
    }
}