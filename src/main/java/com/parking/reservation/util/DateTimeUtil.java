package com.parking.reservation.util;

import java.time.LocalDateTime;

public class DateTimeUtil {
    public static boolean isBetween(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(start) && now.isBefore(end);
    }
}
