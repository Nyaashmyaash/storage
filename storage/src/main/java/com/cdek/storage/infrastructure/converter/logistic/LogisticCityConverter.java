package com.cdek.storage.infrastructure.converter.logistic;

import com.cdek.locality.client.api.dto.CityEsbDto;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.logistic.LogisticCity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { UuidConverter.class })
public interface LogisticCityConverter {

    @Mapping(target = "cityUuid", source = "uuid")
    @Mapping(target = "cityCode", source = "ek4Code")
    LogisticCity fromDto(CityEsbDto dto);
}
