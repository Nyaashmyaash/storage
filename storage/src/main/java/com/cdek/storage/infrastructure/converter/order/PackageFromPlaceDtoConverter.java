package com.cdek.storage.infrastructure.converter.order;

import com.cdek.order.dto.order.PlaceDto;
import com.cdek.storage.model.order.Package;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class PackageFromPlaceDtoConverter {

    @Mapping(target = "packageUuid", source = "id")
    @Mapping(target = "orderUuid", ignore = true)
    @Mapping(target = "packageNumber", source = "packNumber")
    @Mapping(target = "barCode", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    public abstract Package fromDto(PlaceDto dto);

    @AfterMapping
    protected void afterMapping(@MappingTarget Package pack, PlaceDto dto) {
        //Из строки обрезается "[ITM]", т.к в Package поле itmBarCode записывается без префикса
        pack.setItmBarCode(dto.getBarCode().substring(5));
        pack.setDeleted(false);
    }
}
