package com.cdek.storage.model.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель заказа.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    /**
     * Идентификатор заказа.
     */
    String orderUuid;

    /**
     * Номер заказа.
     */
    String orderNumber;

    /**
     * Статус заказа.
     */
    String orderStatusCode;

    /**
     * Код типа заказа.
     */
    String orderTypeCode;

    /**
     * Истинный режим доставки.
     */
    String trueDeliveryModeCode;

    /**
     * Код контрагента-плательщика.
     */
    String payerCode;

    /**
     * Уникальный идентификатор контрагента-плательщика.
     */
    String payerUuid;

    /**
     * Идентификатор договора контрагента-плательщика.
     */
    String payerContractUuid;

    /**
     * Номер договора контрагента-плательщика.
     */
    String payerContractNumber;

    /**
     * Имя продавца (актуально только для заказа типа ИМ)
     */
    String sellerName;

    /**
     * Места.
     */
    List<Package> packages = new ArrayList<>();

    /**
     * Кол-во дней, указанных в доп. услуге "Хранение на складе"
     */
    Integer countDay;

    /**
     * Признак удаленного заказа.
     */
    Boolean deleted;

    /**
     * Последнее обновление сущности по шине.
     */
    Instant timestamp;
}
