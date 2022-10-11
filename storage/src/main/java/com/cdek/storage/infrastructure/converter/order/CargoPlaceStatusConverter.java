package com.cdek.storage.infrastructure.converter.order;

import com.cdek.cargoplacelogisticstatus.common.dto.CargoPlaceStatusHistoryDto;
import com.cdek.cargoplacelogisticstatus.common.dto.OrderCargoPlaceDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.order.CargoPlaceStatus;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = { InstantConverter.class,
                UuidConverter.class })
public abstract class CargoPlaceStatusConverter {

    @Mapping(target = "statusUuid", source = "uuid")
    public abstract CargoPlaceStatus fromDto(OrderCargoPlaceDto dto);

    @Mapping(target = "statusUuid", source = "uuid")
    public abstract CargoPlaceStatus fromDto(CargoPlaceStatusHistoryDto dto);

    @AfterMapping
    protected void after(@MappingTarget CargoPlaceStatus cargoPlaceStatus) {
        if (cargoPlaceStatus.getLocationOfficeUuid().equals(StringUtils.EMPTY)) {
            cargoPlaceStatus.setLocationOfficeUuid(null);
        }
    }
}
