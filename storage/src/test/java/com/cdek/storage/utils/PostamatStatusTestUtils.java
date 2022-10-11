package com.cdek.storage.utils;

import com.cdek.omnic.integration.client.dto.PostomatEventDto;
import com.cdek.omnic.integration.client.dto.PostomatOperationEventDto;

import java.time.Instant;
import java.util.UUID;

public class PostamatStatusTestUtils {

    public static UUID EVENT_UUID = UUID.randomUUID();
    public static UUID ORDER_UUID = UUID.randomUUID();
    public static UUID OFFICE_UUID = UUID.randomUUID();
    public static String ORDER_NUMBER = "112233";
    public static String PACKAGE_ID = UUID.randomUUID().toString();
    public static String USER_CODE = "7788";
    public static String DELIVERY_MODE = "RECEIVER_POSTAMAT";
    public static Instant TIMESTAMP = Instant.now();


    public static PostomatEventDto getValidDto() {
        var dto = new PostomatEventDto();
        dto.setUuid(EVENT_UUID);
        dto.setOrderUuid(ORDER_UUID);
        dto.setOrderNumber(ORDER_NUMBER);
        dto.setPackageId(PACKAGE_ID);
        dto.setOperation(PostomatOperationEventDto.POSTED);
        dto.setUserCode(USER_CODE);
        dto.setOfficeUuid(OFFICE_UUID);
        dto.setDeliveryMode(DELIVERY_MODE);
        dto.setOperationTime(TIMESTAMP);
        return dto;
    }

    public static PostomatEventDto getDtoWithNullUUid() {
        var dto = getValidDto();
        dto.setUuid(null);
        return dto;
    }

    public static PostomatEventDto getDtoWithNullOrderUuid() {
        var dto = getValidDto();
        dto.setOrderUuid(null);
        return dto;
    }

    public static PostomatEventDto getDtoWithNullOperation() {
        var dto = getValidDto();
        dto.setOperation(null);
        return dto;
    }

    public static PostomatEventDto getDtoWithNullDeliveryMode() {
        var dto = getValidDto();
        dto.setDeliveryMode(null);
        return dto;
    }

    public static PostomatEventDto getDtoWithNullOperationTime() {
        var dto = getValidDto();
        dto.setOperationTime(null);
        return dto;
    }

    public static PostomatEventDto createEvent(PostomatOperationEventDto operation, String deliveryMode) {
        var dto = getValidDto();
        dto.setOperation(operation);
        dto.setDeliveryMode(deliveryMode);
        return dto;
    }
}
