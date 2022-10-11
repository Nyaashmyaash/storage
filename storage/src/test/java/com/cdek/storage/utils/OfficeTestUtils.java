package com.cdek.storage.utils;

import com.cdek.company.structure.client.dto.OfficeAddressEsbDto;
import com.cdek.storage.infrastructure.stream.dto.OfficeStreamDto;
import com.cdek.storage.model.office.Office;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class OfficeTestUtils {

    public static final UUID OFFICE_UUID = UUID.randomUUID();
    public static final String CITY_CODE = "270";
    public static final String ANY_CITY_CODE = "111";
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    public static Office createOfficeModel() {
        return Office.builder()
                .officeUuid(OFFICE_UUID.toString())
                .cityCode(CITY_CODE)
                .updateTimestamp(TIMESTAMP)
                .build();
    }

    public static Office createUpdatedOfficeModel() {
        Office office = createOfficeModel();
        office.setCityCode(ANY_CITY_CODE);

        return office;
    }

    public static OfficeStreamDto createOfficeEsbEventDto() {
        OfficeAddressEsbDto address = new OfficeAddressEsbDto();
        address.setCityCode(CITY_CODE);

        OfficeStreamDto dto = new OfficeStreamDto();
        dto.setUuid(OFFICE_UUID);
        dto.setAddress(address);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());

        return dto;
    }

    public static OfficeStreamDto createOfficeEsbEventDtoWithoutUuid() {
        OfficeStreamDto dto = createOfficeEsbEventDto();
        dto.setUuid(null);

        return dto;
    }
}
