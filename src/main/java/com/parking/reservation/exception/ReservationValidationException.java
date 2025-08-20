package com.parking.reservation.exception;

public class ReservationValidationException extends IllegalStateException {
    public ReservationValidationException(String s) {
        super(s);
    }

    public ReservationValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
