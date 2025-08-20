package com.parking.reservation.cache;

import com.parking.reservation.db.model.ParkingLotReservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingLotReservationCacheEntry(UUID reservationId, String lotId, LocalDateTime start, LocalDateTime end) {
    public static ParkingLotReservationCacheEntry from(ParkingLotReservation reservation) {
        return new ParkingLotReservationCacheEntry(reservation.id(), reservation.lotId(), reservation.startTime(), reservation.endTime());
    }
}
