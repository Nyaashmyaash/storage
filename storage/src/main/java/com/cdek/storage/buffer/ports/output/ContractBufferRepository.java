package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.contract.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе с контрактами для буферизации данных.
 */
public interface ContractBufferRepository {

    /**
     * Сохранить новый договор в бд.
     *
     * @param contract объект {@link Contract}.
     */
    void saveNewContract(@Nonnull Contract contract);

    /**
     * Обновить существующий договора.
     *
     * @param contract объект {@link Contract}.
     */
    void updateContract(@Nonnull Contract contract);

    /**
     * Получить временную метку изменения договора.
     *
     * @param contractUuid идентификатор договора.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String contractUuid);
}
