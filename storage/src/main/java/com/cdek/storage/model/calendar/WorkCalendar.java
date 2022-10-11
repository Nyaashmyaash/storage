package com.cdek.storage.model.calendar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

/**
 * Модель производственного календаря.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkCalendar {

    /**
     * Идентификатор кадендаря.
     */
    String calendarUuid;

    /**
     * Активный ли календарь.
     */
    Boolean active;

    /**
     * Дата редактирования.
     */
    Instant dateUpdated;

    /**
     * Год.
     */
    Integer year;

    /**
     * Код страны.
     */
    String countryCode;

    /**
     * Уникальный идентификатор страны.
     */
    String countryUuid;

    /**
     * Код региона.
     */
    String regionCode;

    /**
     * Уникальный идентификатор региона.
     */
    String regionUuid;

    /**
     * Список дней года.
     */
    List<WorkCalendarDay> days;
}
