package com.cdek.storage.infrastructure.converter.office;

import com.cdek.company.structure.client.esb.dto.OfficeEsbEventDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.office.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { InstantConverter.class, UuidConverter.class })
public interface OfficeConverter {

    @Mapping(target = "officeUuid", source = "uuid")
    @Mapping(target = "cityCode", source = "address.cityCode")
    @Mapping(target = "updateTimestamp", source = "timestamp")
    Office fromDto(OfficeEsbEventDto dto);
}
