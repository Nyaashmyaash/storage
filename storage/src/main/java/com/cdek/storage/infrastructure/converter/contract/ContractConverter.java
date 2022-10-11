package com.cdek.storage.infrastructure.converter.contract;

import com.cdek.contract.esb.client.ContractEventDto;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.contract.Contract;
import com.cdek.storage.model.contract.Seller;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        SellerConverter.class,
        UuidConverter.class })
public abstract class ContractConverter {

    @Mapping(target = "contractUuid", source = "uuid")
    @Mapping(target = "contragentUuid", source = "clientUuid")
    @Mapping(target = "timestamp", source = "updateTimestamp")
    public abstract Contract fromDto(ContractEventDto dto);

    @AfterMapping
    protected void after(@MappingTarget Contract contractEntity) {
        final List<Seller> sellers = contractEntity.getSellers();

        if (CollectionUtils.isNotEmpty(sellers)) {
            sellers.forEach(item -> {
                item.setContractUuid(contractEntity.getContractUuid());
                item.setTimestamp(contractEntity.getTimestamp());
            });
        }
    }
}
