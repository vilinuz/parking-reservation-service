package com.parking.reservation.service;

import com.parking.reservation.TestData;
import com.parking.reservation.cache.ReservationRedisCache;
import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.repo.ParkingLotReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.parking.reservation.db.model.ReservationStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParkingReservationSchedulerTest {

    @Mock
    private ParkingLotReservationRepository reservationRepository;

    @Mock
    private ReservationRedisCache cacheService;

    private ParkingReservationScheduler scheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        scheduler = new ParkingReservationScheduler(reservationRepository, cacheService);
    }

    @Test
    void testScheduleActivation_NotNegativeDelay() {
        // Reservation in the future
        ParkingLotReservation reservation = new ParkingLotReservation(
                UUID.randomUUID(), "A1", LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(10), NEW, "SA1234ML");

        // Mocking repository/cache behaviors
        ParkingLotReservation activated = reservation.activate();
        when(reservationRepository.save(any())).thenReturn(Mono.just(activated));
        when(cacheService.create(any(), any())).thenReturn(Mono.empty());
        when(cacheService.delete(any())).thenReturn(Mono.empty());

        // Directly test activateReservation
        Mono<ParkingLotReservation> mono = scheduler.activateReservation(reservation);

        StepVerifier.create(mono)
                .expectNextMatches(res -> res.status() == ACTIVE)
                .verifyComplete();

        verify(reservationRepository, times(1)).save(any(ParkingLotReservation.class));
        verify(cacheService, times(1)).create(anyString(), any());
        verify(cacheService, times(1)).delete(anyString());
    }

    @Test
    void testScheduleExpiration_NotNegativeDelay() {
        // Reservation ending in a minute
        ParkingLotReservation reservation = new ParkingLotReservation(
                UUID.randomUUID(), "A1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), ACTIVE, "SA1234ML");

        ParkingLotReservation expired = reservation.expire();
        when(reservationRepository.save(any())).thenReturn(Mono.just(TestData.createDefaultReservation(COMPLETED)));
        when(cacheService.delete(any())).thenReturn(Mono.empty());

        Mono<ParkingLotReservation> mono = scheduler.expireReservation(reservation);

        StepVerifier.create(mono)
                .expectNextMatches(res -> res.status() == COMPLETED)
                .verifyComplete();

        verify(reservationRepository, times(1)).save(any(ParkingLotReservation.class));
        verify(cacheService, times(1)).delete(anyString());
    }

    @Test
    void testScheduleActivation_AlreadyActive() {
        // Reservation that's active now
        LocalDateTime now = LocalDateTime.now();
        ParkingLotReservation reservation = new ParkingLotReservation(
                UUID.randomUUID(), "A1", now.minusMinutes(1), now.plusMinutes(10), NEW, "SA1234ML");

        ParkingLotReservation activated = reservation.activate();
        when(reservationRepository.save(any())).thenReturn(Mono.just(activated));
        when(cacheService.create(any(), any())).thenReturn(Mono.empty());
        when(cacheService.delete(any())).thenReturn(Mono.empty());

        // Call scheduleActivation (should call activateReservation immediately)
        scheduler.scheduleActivation(reservation);

        verify(reservationRepository, times(1)).save(any(ParkingLotReservation.class));
    }
}
