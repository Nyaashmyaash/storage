package com.cdek.storage.buffer.ports.input;

import com.cdek.work.calendar.client.dto.CalendarEsbDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface WorkCalendarRefresher {

    void refreshIfNeeded(@Nonnull CalendarEsbDto newDto, @Nullable Instant current);
}
