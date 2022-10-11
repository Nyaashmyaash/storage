package com.cdek.storage.model.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Модель продавца.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seller {

    /**
     * Идентификатор продавца
     */
    Long id;

    /**
     * Идентификатор контракта.
     */
    String contractUuid;

    /**
     * Имя продавца.
     */
    String sellerName;

    /**
     * Индивидуальные условия сроков бесплатного хранения.
     */
    int freeStorageDaysCount;

    /**
     * Индивидуальные условия сроков бесплатного хранения для постаматов.
     */
    int postamatOrderStorageDaysCount;

    /**
     * Последнее обновление сущности по шине.
     */
    Instant timestamp;
}
