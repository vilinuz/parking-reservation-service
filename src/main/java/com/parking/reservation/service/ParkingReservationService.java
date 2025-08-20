package com.parking.reservation.service;

import com.parking.reservation.api.model.ParkingLotDto;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;
import com.parking.reservation.util.DateTimeUtil;
import com.parking.reservation.validation.ParkingReservationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static com.parking.reservation.cache.ReservationRedisCache.createActiveCacheKey;
import static com.parking.reservation.cache.ReservationRedisCache.createNewCacheKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingReservationService {

    private final ParkingReservationValidator parkingReservationValidator;
    private final ParkingReservationScheduler parkingReservationScheduler;
    private final ParkingReservationStore parkingReservationStore;

    @Transactional
    public Mono<Void> createReservation(ParkingLotReservation parkingLotReservation) {
        return parkingReservationValidator.validateAll(parkingLotReservation)
                .then(Mono.defer(() ->
                        parkingReservationStore.persist(identifyRedisKey(parkingLotReservation), parkingLotReservation)
                                .flatMap(parkingReservationScheduler::schedule)
                                .doOnError(e -> log.error("Failed to create reservation {}", parkingLotReservation, e))
                                .then()));
    }

    private String identifyRedisKey(ParkingLotReservation reservation) {
        return DateTimeUtil.isBetween(reservation.startTime(), reservation.endTime()) ?
                createActiveCacheKey(reservation.lotId()) : createNewCacheKey(reservation.lotId());
    }

    public Flux<ParkingLotDto> findAvailableParkingLots(LocalDateTime start, LocalDateTime end) {
        return parkingReservationStore
                .findAvailableParkingLots(start, end)
                .map(lotId -> new ParkingLotDto(lotId, true));
    }

    public Flux<ParkingLotReservation> findReservationsByStatus(ReservationStatus status) {
        return parkingReservationStore.findReservationsByStatus(status);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleAll() {
        parkingReservationStore.findReservationsByStatuses(List.of(ReservationStatus.NEW, ReservationStatus.ACTIVE))
                .flatMap(parkingReservationStore::loadToCache)
                .doOnNext(parkingReservationScheduler::schedule)
                .subscribe();
    }
}
