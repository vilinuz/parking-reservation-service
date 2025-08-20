package com.parking.reservation.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponseDto(UUID reservationId,
                                     String lotId,
                                     String vehiclePlate,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime,
                                     ReservationStatusDto status) {
}
