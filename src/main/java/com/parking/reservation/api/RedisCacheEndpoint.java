package com.parking.reservation.api;

import com.parking.reservation.cache.ParkingLotReservationCacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import java.util.Map;

@Component
@Endpoint(id = "redisCache")
@RequiredArgsConstructor
public class RedisCacheEndpoint {

    private final ReactiveRedisTemplate<String, ParkingLotReservationCacheEntry> redisTemplate;

    @ReadOperation
    public Flux<Map<String, Object>> showCache() {
        // Example: List all keys and their values (for string caches)
        return redisTemplate.keys("*")
                .flatMap(key -> redisTemplate.opsForSet().members(key)
                        .map(value -> Map.of("key", key, "value", value)));
    }
}
