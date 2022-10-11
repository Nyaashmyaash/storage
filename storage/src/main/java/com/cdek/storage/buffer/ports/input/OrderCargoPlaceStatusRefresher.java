package com.cdek.storage.buffer.ports.input;

import com.cdek.storage.infrastructure.stream.dto.OrderCargoPlaceStreamDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface OrderCargoPlaceStatusRefresher {

    void saveIfNeeded(@Nonnull OrderCargoPlaceStreamDto newDto, @Nullable Instant current);
}
