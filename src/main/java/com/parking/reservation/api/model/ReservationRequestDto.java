package com.parking.reservation.api.model;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ReservationRequestDto(@NotBlank @Min(4) @Max(4) @Pattern(regexp = "^[A-D](100|[1-9][0-9]?)$",
        message = "lotId must be A-D followed by a number from 1 to 100")
                                    String lotId,
                                    @NotBlank @Min(8) @Max(8) @Pattern(
                                            regexp = "^[A-Za-z]{2}[0-9]{4}[A-Za-z]{2}$",
                                            message = "Plate must be two letters, four digits, then two letters (e.g., AB1234CD)")
                                    String vehiclePlate,
                                    @NotNull @FutureOrPresent
                                    LocalDateTime startTime,
                                    @NotNull @Future
                                    LocalDateTime endTime
) {
}
