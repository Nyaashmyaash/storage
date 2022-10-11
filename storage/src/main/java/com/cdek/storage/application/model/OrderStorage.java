package com.cdek.storage.application.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Модель срока хранения заказа.
 * <a href="https://confluence.cdek.ru/pages/viewpage.action?pageId=78911453">Аналитика</a>
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStorage {

    /**
     * Идентификатор сущности "Сроки хранения заказа".
     */
    String orderStorageUuid;

    /**
     * Идентификатор заказа.
     */
    String orderUuid;

    /**
     * Номер заказа.
     */
    String orderNumber;

    /**
     * Крайняя дата хранения на складе.
     */
    Instant deadlineForStorage;

    /**
     * Дата получения в офисе доставки (или дата закладки в постамат).
     */
    Instant dateOfReceiptInDeliveryOfficeOrPostamat;

    /**
     * Срок хранения заказа в днях на складе или при закладке в постамат.
     */
    Integer shelfLifeOrderInDays;

    /**
     * Дата и время создания/редактирования сущности
     */
    Instant timestamp;
}
