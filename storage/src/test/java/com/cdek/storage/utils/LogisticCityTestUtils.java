package com.cdek.storage.utils;

import com.cdek.locality.client.api.dto.CityEsbDto;
import com.cdek.storage.model.logistic.LogisticCity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class LogisticCityTestUtils {

    public static final UUID CITY_UUID = UUID.randomUUID();
    public static final UUID COUNTRY_UUID = UUID.randomUUID();
    public static final UUID ANY_COUNTRY_UUID = UUID.randomUUID();
    public static final UUID REGION_UUID = UUID.randomUUID();
    public static final int CITY_CODE = 270;
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final ZoneId TIME_ZONE_ID = ZoneId.of("Asia/Krasnoyarsk");

    public static LogisticCity createLogisticCityModel() {
        return LogisticCity.builder()
                .cityUuid(CITY_UUID.toString())
                .cityCode(CITY_CODE)
                .timeZone(TIME_ZONE_ID)
                .regionUuid(REGION_UUID.toString())
                .countryUuid(COUNTRY_UUID.toString())
                .updateTimestamp(TIMESTAMP)
                .build();
    }

    public static LogisticCity createUpdatedLogisticCityModel() {
        LogisticCity city = createLogisticCityModel();
        city.setCountryUuid(ANY_COUNTRY_UUID.toString());

        return city;
    }

    public static CityEsbDto createCityEsbDto() {
        CityEsbDto dto = new CityEsbDto();
        dto.setUuid(CITY_UUID);
        dto.setEk4Code(CITY_CODE);
        dto.setTimeZone(TIME_ZONE_ID);
        dto.setRegionUuid(REGION_UUID);
        dto.setCountryUuid(COUNTRY_UUID);
        dto.setUpdateTimestamp(TIMESTAMP);

        return dto;
    }

    public static CityEsbDto createCityEsbDtoWithoutUuid() {
        CityEsbDto dto = createCityEsbDto();
        dto.setUuid(null);

        return dto;
    }
}
