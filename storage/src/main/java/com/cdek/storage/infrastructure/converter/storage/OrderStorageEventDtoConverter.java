package com.cdek.storage.infrastructure.converter.storage;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.client.esb.OrderStorageEventDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UuidConverter.class, InstantConverter.class })
public interface OrderStorageEventDtoConverter {

    @Mapping(target = "uuid", source = "orderStorageUuid")
    OrderStorageEventDto fromOrderStorage(OrderStorage orderStorage);
}
