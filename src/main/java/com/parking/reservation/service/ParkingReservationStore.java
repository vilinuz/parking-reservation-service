package com.parking.reservation.service;

import com.parking.reservation.cache.ReservationRedisCache;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;
import com.parking.reservation.db.repo.ParkingLotReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.parking.reservation.cache.ParkingLotReservationCacheEntry.from;
import static com.parking.reservation.cache.ReservationRedisCache.createActiveCacheKey;
import static com.parking.reservation.cache.ReservationRedisCache.createNewCacheKey;

@Component
@RequiredArgsConstructor
public class ParkingReservationStore {
    private final ParkingLotReservationRepository reservationRepository;
    private final ReservationRedisCache reservationCache;

    public Mono<ParkingLotReservation> persist(String key, ParkingLotReservation parkingLotReservation) {
        return reservationRepository.save(parkingLotReservation)
                .flatMap(reservation ->
                        reservationCache.create(key, from(reservation))
                                .thenReturn(reservation));
    }

    public Mono<ParkingLotReservation> loadToCache(ParkingLotReservation reservation) {
        return switch (reservation.status()) {
            case NEW -> reservationCache.create(createNewCacheKey(reservation.lotId()), from(reservation))
                    .thenReturn(reservation);
            case ACTIVE -> reservationCache.create(createActiveCacheKey(reservation.lotId()), from(reservation))
                    .thenReturn(reservation);
            case COMPLETED -> null;
        };
    }

    public Flux<String> findAvailableParkingLots(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findAvailableParkingLotIds(start, end);
    }

    public Flux<ParkingLotReservation> findReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public Flux<ParkingLotReservation> findReservationsByStatuses(Collection<ReservationStatus> statuses) {
        return reservationRepository.findByStatusIn(statuses);
    }

    public Mono<Long> countActiveReservations() {
        return reservationRepository.countByStatus(ReservationStatus.ACTIVE);
    }
}
