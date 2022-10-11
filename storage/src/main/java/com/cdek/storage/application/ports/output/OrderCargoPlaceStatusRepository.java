package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.order.CargoPlaceStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Репозиторий по работе со статусами ГМ.
 */
public interface OrderCargoPlaceStatusRepository {

    /**
     * Найти статус ГМ по идентификатору.
     *
     * @param statusUuid идентификатор статуса ГМ.
     * @return объект {@link CargoPlaceStatus}.
     */
    @Nullable
    CargoPlaceStatus findStatusByUuid(@Nonnull String statusUuid);

    /**
     * Найти текущий статус ГМ по идентификатору ГМ.
     *
     * @param packageUuid идентификатор ГМ.
     * @return объект {@link CargoPlaceStatus}.
     */
    @Nullable
    CargoPlaceStatus findCurrentStatusByPackageUuid(@Nonnull String packageUuid);

    /**
     * Найти код текущего статуса ГМ по идентификатору ГМ.
     *
     * @param packageUuid идентификатор грузоместа.
     * @return код статуса грузоместа.
     */
    @Nullable
    String findCurrentStatusCodeByPackageUuid(@Nonnull String packageUuid);

    /**
     * Получить идентификатор офиса, в котором лежит одно из мест заказа.
     * @param orderUuid идентификатор заказа.
     * @return идетификатор офиса.
     */
    @Nonnull
    String getCurrentOfficeUuidByOrderUuid(@Nonnull String orderUuid);

    /**
     * Получить все конкретные статусы ГМ по идентификатору ГМ.
     *
     * @param packageUuid идентификатор ГМ.
     * @return список {@link CargoPlaceStatus}.
     */
    @Nonnull
    List<String> getAllStatusListByPackageUuidAndStatus(@Nonnull String packageUuid, @Nonnull String status);
}
