package com.cdek.storage.buffer.ports.input;

import com.cdek.storage.infrastructure.stream.dto.OrderStreamDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface OrderRefresher {

    void refreshIfNeeded(@Nonnull OrderStreamDto newDto, @Nullable Instant current);
}
