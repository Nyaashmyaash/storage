package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.calendar.WorkCalendarDay;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Репозиторий по работе с днями производственного календаря для буферизации данных.
 */
public interface WorkCalendarDayBufferRepository {

    /**
     * Сохранить новые дни календаря в бд списком.
     *
     * @param calendarDaysList лист объектов {@link WorkCalendarDay}.
     */
    void saveNewCalendarDays(@Nonnull List<WorkCalendarDay> calendarDaysList);

    /**
     * Обновить существующие дни календаря в бд списком.
     *
     * @param calendarDaysList лист объектов {@link WorkCalendarDay}.
     */
    void updateCalendarDays(@Nonnull List<WorkCalendarDay> calendarDaysList);
}
