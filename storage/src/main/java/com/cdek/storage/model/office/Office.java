package com.cdek.storage.model.office;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Модель офиса.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Office {

    /**
     * Идентификатор офиса.
     */
    String officeUuid;

    /**
     * Код города из ЭК4.
     */
    String cityCode;

    /**
     * Дата и время последнего обновления сущности.
     */
    Instant updateTimestamp;
}
