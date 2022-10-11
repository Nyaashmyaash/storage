package com.cdek.storage.infrastructure.converter.common;

import org.mapstruct.Mapper;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UuidConverter {

    /**
     * Конвертация String в UUID.
     *
     * @param s значение String.
     * @return UUID.
     */
    default UUID map(String s) {
        return Optional.ofNullable(s).map(UUID::fromString).orElse(null);
    }

    /**
     * Конвертация UUID в String.
     *
     * @param uuid значение UUID.
     * @return string.
     */
    default String map(UUID uuid) {
        return Optional.ofNullable(uuid).map(UUID::toString).orElse(null);
    }
}
