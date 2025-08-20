package com.parking.reservation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "parking")
public class ParkingReservationConfigProperties {
    private int capacity = 100;
    private int maxUsagePercent = 80;
    private int maxBookingDuration = 3600;

    public int getMaxAllowedUsage() {
        return (capacity * maxUsagePercent) / 100;
    }
}
