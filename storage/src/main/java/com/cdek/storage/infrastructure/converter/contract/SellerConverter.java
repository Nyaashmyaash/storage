package com.cdek.storage.infrastructure.converter.contract;

import com.cdek.contract.esb.client.seller.SellerDto;
import com.cdek.storage.model.contract.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SellerConverter {

    @Mapping(target = "contractUuid", ignore = true)
    @Mapping(target = "sellerName", source = "name")
    @Mapping(target = "freeStorageDaysCount", source = "refundRule.freeStorageDaysCount")
    @Mapping(target = "postamatOrderStorageDaysCount", source = "postamatOrderStorageDaysCount")
    @Mapping(target = "timestamp", ignore = true)
    Seller fromDto(SellerDto dto);
}
