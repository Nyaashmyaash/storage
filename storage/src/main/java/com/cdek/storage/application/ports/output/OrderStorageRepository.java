package com.cdek.storage.application.ports.output;

import com.cdek.storage.application.model.OrderStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

/**
 * Репозиторий по работе со сроками хранени заказа.
 */
public interface OrderStorageRepository {

    /**
     * Сохранить новые сроки хранения заказа.
     *
     * @param orderStorage сущность сроков храенения.
     */
    void saveNewOrderStorage(@Nonnull OrderStorage orderStorage);

    /**
     * Обновить сроки хранения заказа.
     *
     * @param orderStorage сущность сроков храенения.
     */
    void updateOrderStorage(@Nonnull OrderStorage orderStorage);

    /**
     * Получить временную метку изменения сроков хранения заказа.
     *
     * @param orderStorageUuid идентификатор сущности сроков хранения заказа.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String orderStorageUuid);

    /**
     * Поиск срока хранения заказа по идентификатору.
     *
     * @param orderStorageUuid идентификатор срока хранения.
     * @return объект {@link OrderStorage}.
     */
    @Nullable
    OrderStorage findOrderStorageByUuid(@Nonnull String orderStorageUuid);

    /**
     * Существует ли расчет сроков хранения заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return да\нет.
     */
    boolean isOrderStorageExists(@Nonnull String orderUuid);

    /**
     * Поиск срока хранения заказа по идентификатору заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return объект {@link OrderStorage}.
     */
    @Nullable
    OrderStorage findOrderStorageByOrderUuid(@Nonnull String orderUuid);

    /**
     * Получение срока хранения заказа по идентификатору заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return объект {@link OrderStorage}.
     */
    @Nonnull
    OrderStorage getOrderStorageByOrderUuid(@Nonnull String orderUuid);

    /**
     * Поиск срока хранения заказа по номеру заказа.
     *
     * @param orderNumber номер заказа.
     * @return объект {@link OrderStorage}.
     */
    @Nullable
    OrderStorage findOrderStorageByOrderNumber(@Nonnull String orderNumber);

    /**
     * Получение списка сроков хранения заказа.
     *
     * @param dateFrom с какой даты делать выборку.
     * @param dateTo   по какую дату делать выборку.
     * @param limit    лимит.
     * @return список {@link OrderStorage}.
     */
    @Nonnull
    List<OrderStorage> getOrderStorageList(@Nonnull Instant dateFrom, @Nonnull Instant dateTo, int limit);

    /**
     * Удалить срок хранения.
     *
     * @param orderNumber номер заказа.
     */
    void deleteOrderStoragePeriod(@Nonnull String orderNumber);

    /**
     * Получение списка идентификаторов заказов, у которых более одного срока хранения.
     *
     * @param limit лимит.
     * @return список идентификаторов.
     */
    @Nonnull
    List<String> getOrderNumberWithDuplicateStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo,
            int limit);
}
