package com.cdek.storage.infrastructure.stream.dto;

//TODO: удалить обертку когда предоставят DTO с имплементацией ExchangeObject

import com.cdek.cargoplacelogisticstatus.common.dto.OrderCargoPlaceDto;
import com.cdek.stream.lib.ExchangeObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

/**
 * Обертка над OrderCargoPlaceDto, которая реализует ExchangeObject.
 * Нужна для того, чтобы воспользоваться CDEK stream-lib.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderCargoPlaceStreamDto extends OrderCargoPlaceDto implements ExchangeObject {

    @Override
    public Instant getUpdateTimestamp() {
        return Optional.ofNullable(super.getTimestamp())
                .map(Instant::ofEpochMilli)
                .orElse(null);
    }
}
