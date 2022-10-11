package com.cdek.storage.infrastructure.converter.order;

import com.cdek.catalog.common.entity.AdditionalServiceType;
import com.cdek.catalog.common.entity.OrderType;
import com.cdek.catalog.common.entity.PayerType;
import com.cdek.order.dto.order.AdditionalServiceDto;
import com.cdek.order.dto.order.AdditionalServiceParamDto;
import com.cdek.order.dto.order.OrderDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.order.Order;
import com.cdek.storage.model.order.Package;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = { InstantConverter.class,
                PackageFromPlaceDtoConverter.class,
                UuidConverter.class })
public abstract class OrderDtoConverter {

    @Mapping(target = "orderUuid", source = "main.id")
    @Mapping(target = "orderNumber", source = "main.orderNumber")
    @Mapping(target = "orderStatusCode", ignore = true)
    @Mapping(target = "orderTypeCode", source = "main.orderType")
    @Mapping(target = "trueDeliveryModeCode", source = "main.trueDeliveryModeCode")
    @Mapping(target = "payerUuid", source = "payer.id")
    @Mapping(target = "payerContractUuid", ignore = true)
    @Mapping(target = "payerContractNumber", ignore = true)
    @Mapping(target = "sellerName", source = "cargo.shopSellerName")
    @Mapping(target = "packages", source = "cargo.places")
    @Mapping(target = "countDay", ignore = true)
    @Mapping(target = "deleted", source = "main.deleted", defaultValue = "false")
    public abstract Order fromOrderDto(OrderDto dto);

    @AfterMapping
    protected void afterMapping(@MappingTarget Order orderEntity, OrderDto dto) {
        orderEntity.setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        final List<Package> packages = orderEntity.getPackages();
        if (CollectionUtils.isNotEmpty(packages)) {
            packages.forEach(item -> {
                item.setOrderUuid(orderEntity.getOrderUuid());
                item.setTimestamp(orderEntity.getTimestamp());
            });
        }

        final Collection<AdditionalServiceDto> additionalServiceList = dto.getServices().getAdditionalServices();
        if (additionalServiceList != null) {
            additionalServiceList.stream()
                    .filter(service -> AdditionalServiceType.AS_WAREHOUSING_ALIAS.equals(service.getCode()))
                    .findFirst()
                    .flatMap(services -> services.getParams().stream()
                    .findFirst()
                    .map(AdditionalServiceParamDto::getValue))
                    .ifPresent(count -> orderEntity.setCountDay(Integer.parseInt(count)));
        }

        //Если тип заказа - ИМ, то проверяется кто указан плательщиком: отправитель, получать или третье лицо.
        //Далее берутся данные из соответствующего блока.
        final String payerType = dto.getPayer().getType();
        if(OrderType.ORDER_TYPE_ONLINE_SHOP.equals(dto.getMain().getOrderType())) {
            if (PayerType.OTHER.equals(payerType)) {
                orderEntity.setPayerContractNumber(dto.getOther().getContragent().getContractNumber());
            } else if (PayerType.RECEIVER.equals(payerType)) {
                orderEntity.setPayerContractNumber(dto.getReceiver().getContragent().getContractNumber());
            } else if (PayerType.SENDER.equals(payerType)) {
                orderEntity.setPayerContractNumber(dto.getSender().getContragent().getContractNumber());
            }
        }
    }
}
