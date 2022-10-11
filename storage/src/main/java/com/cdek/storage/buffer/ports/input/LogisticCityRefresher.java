package com.cdek.storage.buffer.ports.input;

import com.cdek.locality.client.api.dto.CityEsbDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface LogisticCityRefresher {

    void refreshIfNeeded(@Nonnull CityEsbDto newDto, @Nullable Instant current);
}
