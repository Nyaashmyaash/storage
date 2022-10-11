package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.contract.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Репозиторий по работе с договором.
 */
public interface ContractRepository {

    /**
     * Найти договор по идентификатору.
     *
     * @param contractUuid идентификатор договора.
     * @return объект {@link Contract}.
     */
    @Nullable
    Contract findContractByUuid(@Nonnull String contractUuid);

    /**
     * Получить идентификатор договора по номеру договора.
     *
     * @param contractNumber номер договора.
     * @return номер контракта.
     */
    @Nonnull
    String getContractUuidByContractNumber(@Nonnull String contractNumber);
}
