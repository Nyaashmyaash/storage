package com.cdek.storage.model.contract;

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
 * Модель договора.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract {

    /**
     * Идентификатор договора.
     */
    String contractUuid;

    /**
     * Идентификатор договора.
     */
    Long id;

    /**
     * Номер договора.
     */
    String number;

    /**
     * Код типа договора.
     */
    String typeCode;

    /**
     * Код статуса договора.
     */
    String statusCode;

    /**
     * Идентификатор контрагента, с которым заключается договор.
     */
    String contragentUuid;

    /**
     * Список продавцов, указанных в договоре ИМ.
     */
    List<Seller> sellers = new ArrayList<>();

    /**
     * Последнее обновление сущности по шине.
     */
    Instant timestamp;
}
