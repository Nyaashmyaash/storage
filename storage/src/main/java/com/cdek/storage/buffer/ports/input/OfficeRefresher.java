package com.cdek.storage.buffer.ports.input;

import com.cdek.storage.infrastructure.stream.dto.OfficeStreamDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface OfficeRefresher {

    void refreshIfNeeded(@Nonnull OfficeStreamDto newDto, @Nullable Instant current);
}
