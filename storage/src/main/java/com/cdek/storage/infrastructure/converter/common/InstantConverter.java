package com.cdek.storage.infrastructure.converter.common;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface InstantConverter {
    /**
     * Конвертация timestamp в Instant.
     *
     * @param timestamp значение timestamp.
     * @return Instant.
     */
    default Instant map(Long timestamp) {
        return Optional.ofNullable(timestamp).map(Instant::ofEpochMilli).orElse(null);
    }

    /**
     * Конвертация Instant в timestamp.
     *
     * @param instant значение Instant.
     * @return timestamp.
     */
    default Long map(Instant instant) {
        return instant.toEpochMilli();
    }
}
