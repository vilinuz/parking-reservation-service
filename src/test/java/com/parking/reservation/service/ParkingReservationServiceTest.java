package com.parking.reservation.service;

import com.parking.reservation.db.model.ParkingLotReservation;
import com.parking.reservation.db.model.ReservationStatus;
import com.parking.reservation.validation.ParkingReservationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static com.parking.reservation.TestData.createDefaultReservation;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ParkingReservationServiceTest {

    @Mock
    private ParkingReservationValidator validator;

    @Mock
    private ParkingReservationScheduler scheduler;

    @Mock
    private ParkingReservationStore store;

    @InjectMocks
    private ParkingReservationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReservation_success() {
        ParkingLotReservation reservation = createDefaultReservation(ReservationStatus.NEW);

        when(validator.validateAll(reservation)).thenReturn(Mono.empty());
        when(store.persist(anyString(), eq(reservation))).thenReturn(Mono.just(reservation));

        StepVerifier.create(service.createReservation(reservation))
                .verifyComplete();

        // schedule should be called on success
        verify(scheduler, times(1)).schedule(reservation);
        verify(store, times(1)).persist(anyString(), eq(reservation));
    }

    @Test
    void testCreateReservation_validationFails() {
        ParkingLotReservation reservation = createDefaultReservation(ReservationStatus.NEW);
        Exception error = new IllegalArgumentException("Invalid");

        when(validator.validateAll(reservation)).thenReturn(Mono.error(error));

        StepVerifier.create(service.createReservation(reservation))
                .expectErrorMatches(thr -> thr instanceof IllegalArgumentException && thr.getMessage().equals("Invalid"))
                .verify();

        verify(scheduler, never()).schedule(any());
        verify(store, never()).persist(anyString(), any());
    }

    @Test
    void testFindAvailableParkingLots_success() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        when(store.findAvailableParkingLots(start, end)).thenReturn(Flux.just("A1", "B2"));

        StepVerifier.create(service.findAvailableParkingLots(start, end))
                .expectNextMatches(dto -> dto.lotId().equals("A1") && dto.available())
                .expectNextMatches(dto -> dto.lotId().equals("B2") && dto.available())
                .verifyComplete();
    }

    @Test
    void testFindReservationsByStatus() {
        ParkingLotReservation res1 = createDefaultReservation(ReservationStatus.NEW);
        ParkingLotReservation res2 = createDefaultReservation(ReservationStatus.NEW);

        when(store.findReservationsByStatus(ReservationStatus.NEW)).thenReturn(Flux.just(res1, res2));

        StepVerifier.create(service.findReservationsByStatus(ReservationStatus.NEW))
                .expectNext(res1)
                .expectNext(res2)
                .verifyComplete();

        verify(store, times(1)).findReservationsByStatus(ReservationStatus.NEW);
    }

    @Test
    void testRescheduleAll() {
        ParkingLotReservation r1 = createDefaultReservation(ReservationStatus.NEW);
        when(store.findReservationsByStatuses(List.of(ReservationStatus.NEW, ReservationStatus.ACTIVE)))
                .thenReturn(Flux.just(r1));
        when(store.loadToCache(r1)).thenReturn(Mono.just(r1));

        service.rescheduleAll();

        verify(store, times(1)).findReservationsByStatuses(anyList());
        verify(store, times(1)).loadToCache(r1);
        verify(scheduler, times(1)).schedule(r1);
    }

}
