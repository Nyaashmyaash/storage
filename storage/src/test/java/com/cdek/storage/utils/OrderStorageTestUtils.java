package com.cdek.storage.utils;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.client.esb.OrderStorageEventDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class OrderStorageTestUtils {

    public static final UUID ORDER_STORAGE_UUID_1 = UUID.randomUUID();
    public static final UUID ORDER_STORAGE_UUID_2 = UUID.randomUUID();
    public static final int SHEL_LIFE_ORDER_IN_DAYS = 10;
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final Instant DATE_OF_RECEIPT =
            Instant.now().truncatedTo(ChronoUnit.SECONDS).minus(5, ChronoUnit.DAYS);
    public static final Instant DEADLINE_FOR_STORAGE =
            Instant.now().truncatedTo(ChronoUnit.SECONDS).plus(5, ChronoUnit.DAYS);

    public static OrderStorage createOrderStorage() {
        return OrderStorage.builder()
                .orderStorageUuid(ORDER_STORAGE_UUID_1.toString())
                .orderUuid(OrderTestUtils.ORDER_UUID.toString())
                .orderNumber(OrderTestUtils.ORDER_NUMBER)
                .deadlineForStorage(DEADLINE_FOR_STORAGE)
                .dateOfReceiptInDeliveryOfficeOrPostamat(DATE_OF_RECEIPT)
                .shelfLifeOrderInDays(SHEL_LIFE_ORDER_IN_DAYS)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static OrderStorage createUpdatedOrderStorage() {
        OrderStorage orderStorage = createOrderStorage();
        orderStorage.setShelfLifeOrderInDays(5);

        return orderStorage;
    }

    public static OrderStorage createOrderStorageWithoutDateOfReceipt() {
        OrderStorage orderStorage = createOrderStorage();
        orderStorage.setDateOfReceiptInDeliveryOfficeOrPostamat(null);
        orderStorage.setDeadlineForStorage(null);

        return orderStorage;
    }

    public static OrderStorage createDuplicateOrderStorage() {
        return OrderStorage.builder()
                .orderStorageUuid(ORDER_STORAGE_UUID_2.toString())
                .orderUuid(OrderTestUtils.ORDER_UUID.toString())
                .orderNumber(OrderTestUtils.ORDER_NUMBER)
                .deadlineForStorage(DEADLINE_FOR_STORAGE)
                .dateOfReceiptInDeliveryOfficeOrPostamat(DATE_OF_RECEIPT)
                .shelfLifeOrderInDays(SHEL_LIFE_ORDER_IN_DAYS)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static OrderStorageEventDto createOrderStorageEventDto() {
        OrderStorageEventDto dto = new OrderStorageEventDto();
        dto.setUuid(ORDER_STORAGE_UUID_1);
        dto.setOrderUuid(OrderTestUtils.ORDER_UUID.toString());
        dto.setOrderNumber(OrderTestUtils.ORDER_NUMBER);
        dto.setDeadlineForStorage(DEADLINE_FOR_STORAGE);
        dto.setDateOfReceiptInDeliveryOfficeOrPostamat(DATE_OF_RECEIPT);
        dto.setShelfLifeOrderInDays(SHEL_LIFE_ORDER_IN_DAYS);
        dto.setTimestamp(TIMESTAMP);

        return dto;
    }
}
