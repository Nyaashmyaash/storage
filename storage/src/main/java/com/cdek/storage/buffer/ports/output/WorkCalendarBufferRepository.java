package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.calendar.WorkCalendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе с производственным календарем для буферизации данных.
 */
public interface WorkCalendarBufferRepository {

    /**
     * Сохранить новый календарь в бд.
     *
     * @param calendar лист объектов {@link WorkCalendar}.
     */
    void saveNewWorkCalendar(@Nonnull WorkCalendar calendar);

    /**
     * Обновить существующий календарь в бд.
     *
     * @param calendar лист объектов {@link WorkCalendar}.
     */
    void updateWorkCalendar(@Nonnull WorkCalendar calendar);

    /**
     * Получить временную метку изменения календаря.
     *
     * @param calendarUuid идентификатор календаря.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String calendarUuid);
}
