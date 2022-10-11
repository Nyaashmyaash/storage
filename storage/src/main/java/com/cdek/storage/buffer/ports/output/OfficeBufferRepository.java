package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.office.Office;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе с офисами для буферизации данных.
 */
public interface OfficeBufferRepository {

    /**
     * Сохранить новый офис в бд.
     *
     * @param officeUuid объект {@link Office}.
     */
    void saveNewOffice(@Nonnull Office officeUuid);

    /**
     * Обновить существующий офис.
     *
     * @param officeUuid объект {@link Office}.
     */
    void updateOffice(@Nonnull Office officeUuid);

    /**
     * Получить временную метку изменения офиса.
     *
     * @param officeUuid идентификатор офиса.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String officeUuid);
}
