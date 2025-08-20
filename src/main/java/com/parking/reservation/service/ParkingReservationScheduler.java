package com.parking.reservation.service;

import com.parking.reservation.cache.ParkingLotReservationCacheEntry;
import com.parking.reservation.cache.ReservationRedisCache;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.repo.ParkingLotReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;

import static com.parking.reservation.cache.ReservationRedisCache.createActiveCacheKey;
import static com.parking.reservation.cache.ReservationRedisCache.createNewCacheKey;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParkingReservationScheduler {

    private final ParkingLotReservationRepository reservationRepository;
    private final ReservationRedisCache cacheService;

    public Mono<Void> schedule(ParkingLotReservation reservation) {
        scheduleActivation(reservation);
        scheduleExpiration(reservation);

        return Mono.empty();
    }

    public void scheduleActivation(ParkingLotReservation parkingLotReservation) {
        // When reservation is about to become active, it gets removed from new:reservations:lot:id: Redis index
        // and reservation entry is created in active:reservations:lot:id: Redis index
        Duration delayToActivation = Duration.between(LocalDateTime.now(), parkingLotReservation.endTime());

        if (!delayToActivation.isNegative()) {
            Mono.delay(Duration.between(LocalDateTime.now(), parkingLotReservation.startTime()))
                    .flatMap(_ -> activateReservation(parkingLotReservation))
                    .doOnSuccess(reservation -> log.info("Reservation {} activated successfully", reservation))
                    .doOnError(reservation -> log.error("Failed to activate reservation {}", reservation.toString()))
                    .subscribe(v -> {
                    }, e -> log.error("Failed to schedule activation", e));
        }
    }

    public void scheduleExpiration(ParkingLotReservation parkingLotReservation) {
        // When reservation is about to become active, it gets removed from new:reservations:lot:id: Redis index
        // and reservation entry is created in active:reservations:lot:id: Redis index
        Duration delayToExpiration = Duration.between(LocalDateTime.now(), parkingLotReservation.endTime());

        if (!delayToExpiration.isNegative()) {
            Mono.delay(Duration.between(LocalDateTime.now(), parkingLotReservation.startTime()))
                    .flatMap(_ -> expireReservation(parkingLotReservation))
                    .doOnSuccess(reservation -> log.info("Reservation {} expired successfully", reservation))
                    .doOnError(reservation -> log.error("Failed to expire reservation {}", reservation.toString()))
                    .subscribe(v -> {
                    }, e -> log.error("Failed to schedule expiration", e));
        }
    }

    Mono<ParkingLotReservation> expireReservation(ParkingLotReservation parkingLotReservation) {
        return reservationRepository.save(parkingLotReservation.expire())
                .flatMap(deleteReservation(createActiveCacheKey(parkingLotReservation.lotId())));
    }

    Mono<ParkingLotReservation> activateReservation(ParkingLotReservation parkingLotReservation) {
        return reservationRepository.save(parkingLotReservation.activate())
                .flatMap(moveReservation(createNewCacheKey(parkingLotReservation.lotId()),
                        createActiveCacheKey(parkingLotReservation.lotId())));
    }

    private Function<ParkingLotReservation, Mono<ParkingLotReservation>> cacheReservation(String key) {
        return reservation -> cacheService
                .create(key, ParkingLotReservationCacheEntry.from(reservation))
                .thenReturn(reservation);
    }

    private Function<ParkingLotReservation, Mono<ParkingLotReservation>> moveReservation(String key, String newKey) {
        return reservation -> cacheService
                .move(key, newKey, ParkingLotReservationCacheEntry.from(reservation))
                .thenReturn(reservation);
    }

    private Function<ParkingLotReservation, Mono<ParkingLotReservation>> deleteReservation(String key) {
        return reservation -> cacheService.delete(key).thenReturn(reservation);
    }
}
