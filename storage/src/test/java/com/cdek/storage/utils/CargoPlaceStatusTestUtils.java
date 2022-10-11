package com.cdek.storage.utils;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.storage.model.order.CargoPlaceStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CargoPlaceStatusTestUtils {

    public static final UUID STATUS_UUID = UUID.randomUUID();
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    public static CargoPlaceStatus createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus status) {
        return CargoPlaceStatus.builder()
                .statusUuid(STATUS_UUID.toString())
                .orderId(OrderTestUtils.ORDER_UUID.toString())
                .packageId(OrderTestUtils.PACKAGE_1_UUID.toString())
                .location("someLocation")
                .locationOfficeUuid("someLocationOfficeUUid")
                .nextLocation("someNextLocation")
                .status(status)
                .timestamp(TIMESTAMP)
                .build();
    }
}
