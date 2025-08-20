package com.parking.reservation.mapping;

import com.parking.reservation.api.model.ReservationResponseDto;
import com.parking.reservation.api.model.ReservationStatusDto;
import com.parking.reservation.db.model.ParkingLotReservation;
import org.springframework.stereotype.Component;

import static com.parking.reservation.mapping.EnumMapper.mapEnum;

@Component
public class ReservationResponseMapper {
    public ReservationResponseDto mapToDto(ParkingLotReservation reservation) {
        return new ReservationResponseDto(
                reservation.id(), reservation.lotId(), reservation.vehiclePlate(), reservation.startTime(),
                reservation.endTime(), mapEnum(reservation.status(), ReservationStatusDto.class));
    }
}
