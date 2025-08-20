package com.parking.reservation.db.repo;

import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ParkingLotReservationRepository extends ReactiveCrudRepository<ParkingLotReservation, String> {
    @Query("""
            SELECT EXISTS (
              SELECT 1 FROM parking_lot_reservation
              WHERE lot_id = $1
                AND status IN ('NEW', 'ACTIVE')
                AND NOT (end_time <= $2 OR start_time >= $3)
            )
            """)
    Mono<Boolean> isParkingLotNewOrActive(
            @Param("lotId") String lotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
            SELECT pl.id
            FROM parking_lot pl
            WHERE NOT EXISTS (
               SELECT 1
               FROM parking_lot_reservation r
               WHERE r.lot_id = pl.id
                 AND r.status IN ('NEW', 'ACTIVE')
                 AND NOT (r.end_time <= $1 OR r.start_time >= $2)
            )
            """)
    Flux<String> findAvailableParkingLotIds(LocalDateTime startTime, LocalDateTime endTime);

    Mono<Long> countByStatus(ReservationStatus status);

    Flux<ParkingLotReservation> findByStatus(ReservationStatus status);

    Flux<ParkingLotReservation> findByStatusIn(Collection<ReservationStatus> statuses);
}

