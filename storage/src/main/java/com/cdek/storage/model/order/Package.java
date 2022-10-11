package com.cdek.storage.model.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Модель упаковки(места).
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Package {

    /**
     * Идентификатор упаковки.
     */
    String packageUuid;

    /**
     * uuid заказа.
     */
    String orderUuid;

    /**
     * Номер упаковки.
     */
    String packageNumber;

    /**
     * Штрих-код упаковки.
     */
    String barCode;

    /**
     * ITM упаковки.
     */
    String itmBarCode;

    /**
     * Признак удаленной упаковки.
     */
    Boolean deleted;

    /**
     * Последнее обновление сущности по шине.
     */
    Instant timestamp;
}
