package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.order.Order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

/**
 * Репозиторий по работе с заказами для буферизации данных.
 */
public interface OrderBufferRepository {

    /**
     * Сохранить новый заказ в бд.
     *
     * @param order объект {@link Order}.
     */
    void saveNewOrder(@Nonnull Order order);

    /**
     * Обновить существующий заказ.
     *
     * @param order объект {@link Order}.
     */
    void updateOrder(@Nonnull Order order);

    /**
     * Получить временную метку изменения заказа.
     *
     * @param orderUuid идентификатор заказа.
     * @return таймстамп.
     */
    @Nullable
    Instant getTimestamp(@Nonnull String orderUuid);

    /**
     * Удаление заказа.
     *
     * @param orderUuid идентификатор заказа.
     */
    void deleteOrderAndPackages(@Nonnull String orderUuid);
}
