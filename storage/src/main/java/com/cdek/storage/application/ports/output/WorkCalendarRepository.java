package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.calendar.WorkCalendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Репозиторий по работе с календарями.
 */
public interface WorkCalendarRepository {

    /**
     * Найти календарь по идентификатору.
     *
     * @param calendarUuid идентификатор календаря.
     * @return объект {@link WorkCalendar}.
     */
    @Nullable
    WorkCalendar findWorkCalendarByUuid(@Nonnull String calendarUuid);

    /**
     * Проверка на существование календаря для конкретного региона и года.
     *
     * @param regionUuid uuid заказа.
     * @param year год.
     * @return да/нет.
     */
    boolean isWorkCalendarExists(@Nonnull String regionUuid, @Nonnull Integer year);
}
