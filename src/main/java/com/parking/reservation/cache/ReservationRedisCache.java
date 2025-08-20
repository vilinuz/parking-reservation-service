package com.parking.reservation.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationRedisCache {
    public static final String ACTIVE_RESERVATIONS_KEY_PREFIX = "active:reservations:lot:id:";
    public static final String NEW_RESERVATIONS_KEY_PREFIX = "new:reservations:lot:id:";

    private final ReactiveRedisTemplate<String, ParkingLotReservationCacheEntry> redisTemplate;

    public Mono<Void> create(String key, ParkingLotReservationCacheEntry reservation) {
        return redisTemplate.opsForSet().add(key, reservation).then();
    }

    public Mono<Void> move(String key, String newKey, ParkingLotReservationCacheEntry reservation) {
        return redisTemplate.opsForSet().move(key, reservation, newKey).then();
    }

    // Optionally, delete reservation by key
    public Mono<Void> delete(String key) {
        return redisTemplate.opsForSet().members(key)
                .filter(reservation -> reservation.end().isBefore(LocalDateTime.now()))
                .flatMap(res -> redisTemplate.opsForSet().remove(key, res)).then();
    }

    public static String createActiveCacheKey(String lotId) {
        return ACTIVE_RESERVATIONS_KEY_PREFIX + lotId;
    }

    public static String createNewCacheKey(String lotId) {
        return NEW_RESERVATIONS_KEY_PREFIX + lotId;
    }
}

