package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.logistic.LogisticCity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе с логистическим городом для буферизации данных.
 */
public interface LogisticCityBufferRepository {

    /**
     * Сохранить новый логистический город в бд.
     *
     * @param logisticCity объект {@link LogisticCity}.
     */
    void saveNewLogisticCity(@Nonnull LogisticCity logisticCity);

    /**
     * Обновить существующий логистический город.
     *
     * @param logisticCity объект {@link LogisticCity}.
     */
    void updateLogisticCity(@Nonnull LogisticCity logisticCity);

    /**
     * Получить временную метку изменения логистического города.
     *
     * @param logisticCityUuid идентификатор логистического города.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String logisticCityUuid);
}
