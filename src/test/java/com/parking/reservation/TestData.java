package com.parking.reservation;

import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestData {
    public static ParkingLotReservation createDefaultReservation(ReservationStatus status) {
        return new ParkingLotReservation(UUID.randomUUID(), "A1", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10), status, "SA111BM");
    }
}
