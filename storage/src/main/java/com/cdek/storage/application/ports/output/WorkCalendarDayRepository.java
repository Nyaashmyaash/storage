package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.calendar.WorkCalendarDay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;

/**
 * Репозиторий по работе с днями календаря.
 */
public interface WorkCalendarDayRepository {

    /**
     * Найти день календаря по идентификатору календаря и дате.
     *
     * @param calendarUuid идентификатор календаря.
     * @param date дата.
     * @return объект {@link WorkCalendarDay}.
     */
    @Nullable
    WorkCalendarDay findWorkCalendarDayByCalendarUuidAndDate(@Nonnull String calendarUuid, @Nonnull String date);

    /**
     * Получить крайний день хранения заказа.
     *
     * @param countryUuid идентификатор страны.
     * @param regionUuid идентификатор региона.
     * @param date дата.
     * @return дата {@link LocalDate}.
     */
    @Nonnull
    WorkCalendarDay getDateInformation(
            @Nonnull String countryUuid,
            @Nullable String regionUuid,
            @Nonnull String date
    );
}
