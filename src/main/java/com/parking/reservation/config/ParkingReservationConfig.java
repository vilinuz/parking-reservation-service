package com.parking.reservation.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parking.reservation.cache.ParkingLotReservationCacheEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class ParkingReservationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(FAIL_ON_IGNORED_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Bean
    public ReactiveRedisTemplate<String, ParkingLotReservationCacheEntry> reactiveRedisTemplate(
            ObjectMapper objectMapper, ReactiveRedisConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<ParkingLotReservationCacheEntry> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ParkingLotReservationCacheEntry.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, ParkingLotReservationCacheEntry> builder =
                RedisSerializationContext.newSerializationContext(StringRedisSerializer.UTF_8);

        RedisSerializationContext<String, ParkingLotReservationCacheEntry> context = builder
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
