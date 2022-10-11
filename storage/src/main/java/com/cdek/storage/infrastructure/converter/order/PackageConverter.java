package com.cdek.storage.infrastructure.converter.order;

import com.cdek.order.esb.client.PackageEventDto;
import com.cdek.storage.model.order.Package;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PackageConverter {

    @Mapping(target = "packageUuid", source = "id")
    @Mapping(target = "orderUuid", ignore = true)
    @Mapping(target = "packageNumber", source = "packageNum")
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "deleted", source = "deleted", defaultValue = "false")
    Package fromEventDto(PackageEventDto dto);
}
