package com.cdek.storage.model.logistic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZoneId;

/**
 * Модель логистического города.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogisticCity {

    /**
     * Уникальный идентификатор логистического города.
     */
    String cityUuid;

    /**
     * Код города из ЭК4.
     */
    Integer cityCode;

    /**
     * Таймзона, в которой находится город.
     */
    ZoneId timeZone;

    /**
     * Uuid региона, в котором находится город.
     */
    String regionUuid;

    /**
     * Uuid страны, в которой находится город.
     */
    String countryUuid;

    /**
     * Последнее обновление сущности по шине.
     */
    Instant updateTimestamp;
}
