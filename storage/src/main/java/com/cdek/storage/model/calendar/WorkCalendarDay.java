package com.cdek.storage.model.calendar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Модель дня календаря.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkCalendarDay {

    /**
     * День календаря.
     */
    LocalDate date;

    /**
     * Тип дня (1-рабочий день, 2-выходной день, 3-предпразничный день, 4-праздничный день).
     */
    String dayTypeCode;

    /**
     * Уникальный идентификатор календаря.
     */
    String calendarUuid;
}
