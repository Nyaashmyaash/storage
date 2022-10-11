package com.cdek.storage.infrastructure.converter.order;

import com.cdek.catalog.common.entity.AdditionalServiceType;
import com.cdek.order.esb.client.AdditionalServiceEventDto;
import com.cdek.order.esb.client.OrderEventDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.order.Order;
import com.cdek.storage.model.order.Package;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = { InstantConverter.class,
                PackageConverter.class,
                UuidConverter.class })
public abstract class OrderConverter {

    @Mapping(target = "orderUuid", source = "uuid")
    @Mapping(target = "orderNumber", source = "number")
    @Mapping(target = "orderStatusCode", ignore = true)
    @Mapping(target = "orderTypeCode", source = "orderTypeCode")
    @Mapping(target = "trueDeliveryModeCode", source = "trueDeliveryModeCode")
    @Mapping(target = "payerUuid", source = "payer.contragentUuid")
    @Mapping(target = "payerContractUuid", source = "payer.contractUuid")
    @Mapping(target = "payerContractNumber", source = "payer.contractNumber")
    @Mapping(target = "sellerName", source = "onlineShop.sellerName")
    @Mapping(target = "deleted", source = "deleted", defaultValue = "false")
    @Mapping(target = "countDay", ignore = true)
    public abstract Order fromDto(OrderEventDto dto);

    @AfterMapping
    protected void after(@MappingTarget Order orderEntity, OrderEventDto dto) {
        final List<Package> packages = orderEntity.getPackages();
        final List<AdditionalServiceEventDto> additionalServices = dto.getAdditionalServices();

        if (additionalServices != null) {
            dto.getAdditionalServices()
                    .stream()
                    .filter(service -> AdditionalServiceType.AS_WAREHOUSING_ALIAS.equals(service.getCode()))
                    .map(AdditionalServiceEventDto::getAdditionalServiceParams)
                    .findFirst()
                    .ifPresent(params -> orderEntity.setCountDay(params.get(0).getValueInt()));
        }

        if (CollectionUtils.isNotEmpty(packages)) {
            packages.forEach(item -> {
                item.setOrderUuid(orderEntity.getOrderUuid());
                item.setTimestamp(orderEntity.getTimestamp());
            });
        }

        //если событие пришло без номера контракта - просто пустая строчка, то эта строчка заменяется на null
        if (StringUtils.EMPTY.equals(orderEntity.getPayerContractNumber())) {
            orderEntity.setPayerContractNumber(null);
        }
    }
}
