package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.order.Order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

/**
 * Репозиторий по работе с заказом.
 */
public interface OrderRepository {

    /**
     * Найти заказ по идентификатору.
     *
     * @param orderUuid идентификатор заказа.
     * @return объект {@link Order}.
     */
    @Nullable
    Order findOrderByUuid(@Nonnull String orderUuid);

    /**
     * Получить код истинного режима доставки по идентификатору заказ.
     *
     * @param orderUuid идентификатор заказа.
     * @return код режима.
     */
    @Nonnull
    String getTrueDeliveryModeCodeByOrderUuid(@Nonnull String orderUuid);

    /**
     * Проверка на существование заказа в БД.
     *
     * @param orderUuid uuid заказа.
     * @return да/нет.
     */
    boolean isOrderExists(@Nonnull String orderUuid);

    /**
     * Получить заказ по идентификатору.
     *
     * @param orderUuid идентификатор заказа.
     * @return объект {@link Order}.
     */
    @Nonnull
    Order getOrderByUuid(@Nonnull String orderUuid);

    /**
     * Получить кол-во дней, указанных в доп. услуге "Хранение на складе".
     *
     * @param orderUuid идентификатор заказа.
     * @return объект {@link Order}.
     */
    @Nullable
    String findCountDay(@Nonnull String orderUuid);

    /**
     * Получить идентификатор заказа по номеру заказа.
     *
     * @param orderNumber номер заказа.
     * @return идентификатор заказа.
     */
    @Nonnull
    String getOrderUuidByOrderNumber(@Nonnull String orderNumber);

    /**
     * Получение списка номеров заказов, у которых нет сроков хранения.
     *
     * @param dateFrom с какой даты делать выборку.
     * @param dateTo   по какую дату делать выборку.
     * @param limit    лимит.
     * @return список номеров.
     */
    @Nonnull
    List<String> getOrderNumberListWithoutStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo, int limit);
}
