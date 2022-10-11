package com.cdek.storage.utils;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.cargoplacelogisticstatus.common.dto.CargoPlaceStatusHistoryDto;
import com.cdek.cargoplacelogisticstatus.common.dto.OrderCargoPlaceDto;
import com.cdek.storage.infrastructure.stream.dto.OrderCargoPlaceStreamDto;
import com.cdek.storage.model.order.CargoPlaceStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderCargoPlaceTestUtils {

    public static final UUID STATUS_UUID_1 = UUID.randomUUID();
    public static final UUID STATUS_UUID_2 = UUID.randomUUID();
    public static final UUID LOCATION_OFFICE_UUID_1 = UUID.randomUUID();
    public static final UUID LOCATION_OFFICE_UUID_2 = UUID.randomUUID();
    public static final String LOCATION = "location";
    public static final String LOCATION_2 = "location_2";
    public static final String NEXT_LOCATION = "nextLocation";
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    public static CargoPlaceStatus createCargoPlaceStatusModel1() {
        return CargoPlaceStatus.builder()
                .statusUuid(STATUS_UUID_1.toString())
                .orderId(OrderTestUtils.ORDER_UUID.toString())
                .packageId(OrderTestUtils.PACKAGE_1_UUID.toString())
                .location(LOCATION)
                .locationOfficeUuid(LOCATION_OFFICE_UUID_1.toString())
                .nextLocation(NEXT_LOCATION)
                .status(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static CargoPlaceStatus createUpdatedCargoPlaceStatusModel1() {
        return createCargoPlaceStatusModel1().toBuilder()
                .location(LOCATION_2)
                .build();

    }

    public static CargoPlaceStatus createCargoPlaceStatusModel2() {
        return CargoPlaceStatus.builder()
                .statusUuid(STATUS_UUID_2.toString())
                .orderId(OrderTestUtils.ORDER_UUID.toString())
                .packageId(OrderTestUtils.PACKAGE_1_UUID.toString())
                .location(LOCATION)
                .locationOfficeUuid(LOCATION_OFFICE_UUID_2.toString())
                .nextLocation(NEXT_LOCATION)
                .status(OrderCargoPlaceLogisticStatus.CREATED)
                .timestamp(TIMESTAMP.plus(2, ChronoUnit.DAYS))
                .build();
    }

    public static CargoPlaceStatus createUpdatedCargoPlaceStatusModel2() {
        return createCargoPlaceStatusModel2().toBuilder()
                .status(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED)
                .build();

    }

    public static OrderCargoPlaceStreamDto createOrderCargoPlaceStatusEvent() {
        OrderCargoPlaceStreamDto dto = new OrderCargoPlaceStreamDto();
        dto.setUuid(STATUS_UUID_1);
        dto.setOrderId(OrderTestUtils.ORDER_UUID.toString());
        dto.setPackageId(OrderTestUtils.PACKAGE_1_UUID.toString());
        dto.setLocation(LOCATION);
        dto.setLocationOfficeUuid(LOCATION_OFFICE_UUID_1.toString());
        dto.setNextLocation(NEXT_LOCATION);
        dto.setStatus(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());

        return dto;
    }

    public static OrderCargoPlaceStreamDto createOrderCargoPlaceNotFinalStatusEvent() {
        var dto = createOrderCargoPlaceStatusEvent();
        dto.setStatus(OrderCargoPlaceLogisticStatus.ACCEPTED_TO_OFFICE_TRANSIT_WAREHOUSE);
        return dto;
    }

    public static OrderCargoPlaceDto createOrderCargoPlaceDto() {
        return createOrderCargoPlaceStatusEvent();
    }

    public static OrderCargoPlaceStreamDto createOrderCargoPlaceStatusEventWithoutUuid() {
        OrderCargoPlaceStreamDto dto = new OrderCargoPlaceStreamDto();
        dto.setUuid(null);

        return dto;
    }

    public static List<String> getStatusList() {
        List<String> list = new ArrayList<>();
        list.add(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED.name());
        list.add(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED.name());
        list.add(OrderCargoPlaceLogisticStatus.CREATED.name());

        return list;
    }

    public static CargoPlaceStatusHistoryDto createCargoPlaceStatusHistoryDto() {
        CargoPlaceStatusHistoryDto dto = new CargoPlaceStatusHistoryDto();
        dto.setUuid(STATUS_UUID_1.toString());
        dto.setOrderId(OrderTestUtils.ORDER_UUID.toString());
        dto.setPackageId(OrderTestUtils.PACKAGE_1_UUID.toString());
        dto.setLocation(LOCATION);
        dto.setLocationOfficeUuid(LOCATION_OFFICE_UUID_1.toString());
        dto.setNextLocation(NEXT_LOCATION);
        dto.setStatus(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());

        return dto;
    }
}
