package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.order.CargoPlaceStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе со статусами грузомест для буферизации данных.
 */
public interface OrderCargoPlaceStatusBufferRepository {

    /**
     * Сохранить новый статус в бд.
     *
     * @param cargoPlaceStatus объект {@link CargoPlaceStatus}.
     */
    void saveNewStatus(@Nonnull CargoPlaceStatus cargoPlaceStatus);

    /**
     * Обновить статус в бд.
     *
     * @param cargoPlaceStatus объект {@link CargoPlaceStatus}.
     */
    void updateStatus(@Nonnull CargoPlaceStatus cargoPlaceStatus);

    /**
     * Получить временную метку изменения статуса.
     *
     * @param cargoPlaceStatusUuid идентификатор статуса.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String cargoPlaceStatusUuid);
}
