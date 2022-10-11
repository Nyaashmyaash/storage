package com.cdek.storage.model.order;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Модель грузоместа со статусом.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CargoPlaceStatus {

    /**
     * Идентификатор статуса.
     */
    String statusUuid;

    /**
     * Идентификатор заказа к которому относится грузоместо.
     */
    String orderId;

    /**
     * UUID соответствующей упаковки в заказе.
     */
    String packageId;

    /**
     * Местонахождение.
     */
    String location;

    /**
     * Идентификатор местонахождения - officeUuid.
     */
    String locationOfficeUuid;

    /**
     * Следующее местонахождение.
     */
    String nextLocation;

    /**
     * Статус грузоместа.
     */
    OrderCargoPlaceLogisticStatus status;

    /**
     * Последнее обновление статуса по шине.
     */
    Instant timestamp;
}
