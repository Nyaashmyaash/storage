package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.order.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Репозиторий по работе с упаковками.
 */
public interface PackageRepository {

    /**
     * Найти упаковку по идентификатору.
     *
     * @param packageUuid идентификатор упаковки.
     * @return объект {@link Package}.
     */
    @Nullable
    Package findPackageByUuid(@Nonnull String packageUuid);

    /**
     * Поиск всех не удаленных упаковок по идентификатору заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return Коллекцию {@link Package}.
     */
    @Nonnull
    List<Package> getNotDeletedPackagesByOrderUuid(@Nonnull String orderUuid);

    /**
     * Поиск всех упаковок по идентификатору заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return Коллекцию {@link Package}.
     */
    @Nullable
    List<Package> findAllPackagesByOrderUuid(@Nonnull String orderUuid);
}
