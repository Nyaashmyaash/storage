package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.order.Package;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Репозиторий по работе с упаковками(местами) для буферизации данных.
 */
public interface PackageBufferRepository {

    /**
     * Сохранить или обновить упаковку в бд.
     *
     * @param pack пакет {@link Package}.
     */
    void saveOrUpdatePackage(@Nonnull Package pack);

    /**
     * Пометить пакеты как удаленные.
     *
     * @param packUuidList лист идентификаторов пакетов.
     */
    void setPackagesIsDeleted(@Nonnull List<String> packUuidList);

    /**
     * Помечает все места заказа как удаленные.
     *
     * @param orderUuid идентификатор заказа.
     */
    void setAllPackagesIsDeleted(@Nonnull String orderUuid);

    /**
     * Получить все неудаленные места заказа из бд.
     *
     * @param orderUuid идентификатор заказа.
     * @return список мест заказ.
     */
    List<String> getAllNotDeletedPackageUuids(@Nonnull String orderUuid);
}
