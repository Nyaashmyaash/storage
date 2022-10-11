package com.cdek.storage.utils;

import com.cdek.catalog.common.entity.AdditionalServiceType;
import com.cdek.catalog.common.entity.OrderStatus;
import com.cdek.catalog.common.entity.PayerType;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.order.dto.order.AdditionalServiceDto;
import com.cdek.order.dto.order.AdditionalServiceParamDto;
import com.cdek.order.dto.order.CargoDto;
import com.cdek.order.dto.order.ContragentDto;
import com.cdek.order.dto.order.MainDto;
import com.cdek.order.dto.order.OrderDto;
import com.cdek.order.dto.order.OtherDto;
import com.cdek.order.dto.order.PayerDto;
import com.cdek.order.dto.order.PlaceDto;
import com.cdek.order.dto.order.ReceiverDto;
import com.cdek.order.dto.order.SenderDto;
import com.cdek.order.dto.order.ServicesDto;
import com.cdek.order.dto.order.response.ResponseOrderDto;
import com.cdek.order.esb.client.AdditionalServiceEventDto;
import com.cdek.order.esb.client.AdditionalServiceParamEventDto;
import com.cdek.order.esb.client.ClientEventDto;
import com.cdek.order.esb.client.OnlineShopEventDto;
import com.cdek.order.esb.client.OrderEventDto;
import com.cdek.order.esb.client.PackageEventDto;
import com.cdek.storage.infrastructure.stream.dto.OrderStreamDto;
import com.cdek.storage.model.order.Order;
import com.cdek.storage.model.order.Package;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderTestUtils {

    public static final UUID ORDER_UUID = UUID.randomUUID();
    public static final UUID PAYER_UUID = UUID.randomUUID();
    public static final UUID UPDATED_PAYER_UUID = UUID.randomUUID();
    public static final UUID PAYER_CONTRACT_UUID = UUID.randomUUID();
    public static final UUID PACKAGE_1_UUID = UUID.randomUUID();
    public static final UUID PACKAGE_2_UUID = UUID.randomUUID();
    public static final UUID PACKAGE_3_UUID = UUID.randomUUID();
    public static final String ORDER_NUMBER = "11223344";
    public static final String ORDER_TYPE_ONLINE_SHOP = "5";
    public static final String TARIFF_MODE_DD = "1";//дверь-дверь
    public static final String PACKAGE1_NUMBER = "number1";
    public static final String PACKAGE2_NUMBER = "number2";
    public static final String PACKAGE3_NUMBER = "number3";
    public static final String PACKAGE1_BARCODE = "barcode1";
    public static final String PACKAGE2_BARCODE = "barcode2";
    public static final String PACKAGE3_BARCODE = "barcode3";
    public static final String PACKAGE1_ITM_BARCODE = "[ITM]barcode1";
    public static final String PACKAGE2_ITM_BARCODE = "[ITM]barcode2";
    public static final String PACKAGE3_ITM_BARCODE = "[ITM]barcode3";
    public static final String SELLER_NAME = "Some Seller Name";
    public static final String DESCRIPTION = "someDescription";
    public static final String PAYER_CONTRACT_NUMBER = "contractNumber";
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDate ORDER_DATE = LocalDate.now();
    public static final int COUNT_DAYS = 15;

    public static Order createOrderModel() {
        return Order.builder()
                .orderUuid(ORDER_UUID.toString())
                .orderNumber(ORDER_NUMBER)
                .orderTypeCode(ORDER_TYPE_ONLINE_SHOP)
                .trueDeliveryModeCode(TARIFF_MODE_DD)
                .payerUuid(PAYER_UUID.toString())
                .payerContractUuid(PAYER_CONTRACT_UUID.toString())
                .sellerName(SELLER_NAME)
                .packages(getPackageList())
                .countDay(COUNT_DAYS)
                .deleted(Boolean.FALSE)
                .timestamp(TIMESTAMP)
                .payerContractNumber(PAYER_CONTRACT_NUMBER)
                .build();
    }

    public static Order createOrderModelWithNullSellerName() {
        Order order = createOrderModel();
        order.setSellerName(null);

        return order;
    }

    public static Order createOrderModelWithEmptySellerName() {
        Order order = createOrderModel();
        order.setSellerName("");

        return order;
    }

    public static Order createOrderModelWithRandomSellerName() {
        Order order = createOrderModel();
        order.setSellerName("RandomSellerName");
        order.setTrueDeliveryModeCode(TariffMode.TARIFF_MODE_DP);

        return order;
    }

    public static Order createOrderModelByTariffMode(String tariffMode) {
        Order order = createOrderModel();
        order.setTrueDeliveryModeCode(tariffMode);

        return order;
    }

    public static Order createOrderModelWithoutContractNumber() {
        Order order = createOrderModel();
        order.setPayerContractNumber(null);

        return order;
    }

    public static Order createOrderModelWithoutServices() {
        Order order = createOrderModel();
        order.setCountDay(null);

        return order;
    }

    public static Order createOrderModelWithCountDay(int count) {
        Order order = createOrderModel();
        order.setCountDay(count);

        return order;
    }

    public static Order createOrderModelWithoutServicesAndWithoutContractNumber() {
        Order order = createOrderModel();
        order.setCountDay(null);
        order.setPayerContractNumber(null);

        return order;
    }

    public static Order getOrder(String tariffMode, Integer countDay, String typeCode) {
        Order order = createOrderModel();
        order.setTrueDeliveryModeCode(tariffMode);
        order.setCountDay(countDay);
        order.setOrderTypeCode(typeCode);

        return order;
    }

    public static Order createOrderWithTrueDeliveryModeTT() {
        Order order = createOrderModel();
        order.setTrueDeliveryModeCode(TariffMode.TARIFF_MODE_TT);

        return order;
    }

    public static Order createOrderWithoutUuidAndNumberOfContract() {
        Order order = createOrderModel();
        order.setPayerContractNumber(null);
        order.setPayerContractUuid(null);

        return order;
    }

    public static Order createDeletedOrderModel() {
        Order deletedOrder = createOrderModel();
        deletedOrder.setDeleted(true);

        return deletedOrder;
    }

    public static Order createOrderForDb() {
        Order order = createOrderModel();
        order.setPackages(Collections.emptyList());

        return order;
    }

    public static Order createUpdatedOrderForDb() {
        Order order = createOrderModel();
        order.setPackages(Collections.emptyList());
        order.setPayerUuid(UPDATED_PAYER_UUID.toString());

        return order;
    }

    public static Package createPackage1Model() {
        return Package.builder()
                .packageUuid(PACKAGE_1_UUID.toString())
                .orderUuid(ORDER_UUID.toString())
                .packageNumber(PACKAGE1_NUMBER)
                .barCode(PACKAGE1_BARCODE)
                .itmBarCode(PACKAGE1_ITM_BARCODE)
                .deleted(Boolean.FALSE)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Package createPackage2Model() {
        return Package.builder()
                .packageUuid(PACKAGE_2_UUID.toString())
                .orderUuid(ORDER_UUID.toString())
                .packageNumber(PACKAGE2_NUMBER)
                .barCode(PACKAGE2_BARCODE)
                .itmBarCode(PACKAGE2_ITM_BARCODE)
                .deleted(Boolean.FALSE)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Package createPackage3Model() {
        return Package.builder()
                .packageUuid(PACKAGE_3_UUID.toString())
                .orderUuid(ORDER_UUID.toString())
                .packageNumber(PACKAGE3_NUMBER)
                .barCode(PACKAGE3_BARCODE)
                .itmBarCode(PACKAGE3_ITM_BARCODE)
                .deleted(Boolean.FALSE)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static List<Package> getPackageList() {
        List<Package> packList = new ArrayList<>();
        packList.add(createPackage1Model());
        packList.add(createPackage2Model());

        return packList;
    }

    public static OrderStreamDto createOrderStreamDto() {
        OrderStreamDto dto = new OrderStreamDto();
        dto.setUuid(ORDER_UUID);
        dto.setNumber(ORDER_NUMBER);
        dto.setOrderStatusCode(OrderStatus.STATUS_CREATED);
        dto.setOrderTypeCode(ORDER_TYPE_ONLINE_SHOP);
        dto.setTrueDeliveryModeCode(TARIFF_MODE_DD);
        ClientEventDto payer = new ClientEventDto();
        payer.setContragentUuid(PAYER_UUID);
        payer.setContractUuid(PAYER_CONTRACT_UUID);
        dto.setPayer(payer);
        List<PackageEventDto> packList = new ArrayList<>();
        PackageEventDto packDto1 = createPackage1EventDto();
        PackageEventDto packDto2 = createPackage2EventDto();
        packList.add(packDto1);
        packList.add(packDto2);
        dto.setPackages(packList);
        dto.setOrderDate(ORDER_DATE);
        dto.setReturned(false);
        dto.setDeleted(false);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());

        return dto;
    }

    public static PackageEventDto createPackage1EventDto() {
        PackageEventDto dto = new PackageEventDto();
        dto.setId(PACKAGE_1_UUID.toString());
        dto.setPackageNum(PACKAGE1_NUMBER);
        dto.setBarCode(PACKAGE1_BARCODE);
        dto.setItmBarCode(PACKAGE1_ITM_BARCODE);
        dto.setDeleted(false);

        return dto;
    }

    public static PackageEventDto createPackage2EventDto() {
        PackageEventDto dto = new PackageEventDto();
        dto.setId(PACKAGE_2_UUID.toString());
        dto.setPackageNum(PACKAGE2_NUMBER);
        dto.setBarCode(PACKAGE2_BARCODE);
        dto.setItmBarCode(PACKAGE2_ITM_BARCODE);
        dto.setDeleted(false);

        return dto;
    }

    public static OrderStreamDto createOrderStreamDtoWithoutUuid() {
        OrderStreamDto dto = createOrderStreamDto();
        dto.setUuid(null);
        return dto;
    }

    public static OrderStreamDto createOrderStreamDtoWithoutNumber() {
        OrderStreamDto dto = createOrderStreamDto();
        dto.setNumber(null);
        return dto;
    }

    public static OrderStreamDto createPackageStreamDtoWithoutPackageId() {
        OrderStreamDto dto = createOrderStreamDto();
        dto.getPackages().get(0).setId(null);
        return dto;
    }

    public static OrderEventDto createOrderEventDto() {
        OrderEventDto dto = new OrderEventDto();
        dto.setUuid(ORDER_UUID);
        dto.setNumber(ORDER_NUMBER);
        dto.setOrderStatusCode(OrderStatus.STATUS_CREATED);
        dto.setOrderTypeCode(ORDER_TYPE_ONLINE_SHOP);
        dto.setTrueDeliveryModeCode(TARIFF_MODE_DD);
        ClientEventDto payer = new ClientEventDto();
        payer.setContragentUuid(PAYER_UUID);
        payer.setContractUuid(PAYER_CONTRACT_UUID);
        dto.setPayer(payer);
        OnlineShopEventDto onlineShopEventDto = new OnlineShopEventDto();
        onlineShopEventDto.setSellerName(SELLER_NAME);
        dto.setOnlineShop(onlineShopEventDto);
        List<PackageEventDto> packList = new ArrayList<>();
        PackageEventDto packDto1 = createPackage1EventDto();
        PackageEventDto packDto2 = createPackage2EventDto();
        packList.add(packDto1);
        packList.add(packDto2);
        dto.setPackages(packList);
        dto.setAdditionalServices(getAdditionalServiceList());
        dto.setOrderDate(ORDER_DATE);
        dto.setReturned(Boolean.FALSE);
        dto.setDeleted(Boolean.FALSE);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());

        return dto;
    }

    public static OrderEventDto createOrder() {
        OrderEventDto dto = createOrderEventDto();
        dto.setAdditionalServices(null);

        return dto;
    }

    public static Package createPackage1ModelFromEsb() {
        return Package.builder()
                .packageUuid(PACKAGE_1_UUID.toString())
                .packageNumber(PACKAGE1_NUMBER)
                .barCode(PACKAGE1_BARCODE)
                .itmBarCode(PACKAGE1_ITM_BARCODE)
                .deleted(false)
                .build();
    }

    private static List<AdditionalServiceEventDto> getAdditionalServiceList() {
        List<AdditionalServiceEventDto> serviceList = new ArrayList<>();

        List<AdditionalServiceParamEventDto> params = new ArrayList<>();
        AdditionalServiceParamEventDto param = new AdditionalServiceParamEventDto();
        param.setValueInt(COUNT_DAYS);
        params.add(param);

        AdditionalServiceEventDto service = new AdditionalServiceEventDto();
        service.setCode(AdditionalServiceType.AS_WAREHOUSING_ALIAS);
        service.setAdditionalServiceParams(params);

        serviceList.add(service);

        return serviceList;
    }

    public static List<String> getOldPackageUuidList() {
        List<String> oldUuidList = new ArrayList<>();
        oldUuidList.add(PACKAGE_1_UUID.toString());
        oldUuidList.add(PACKAGE_2_UUID.toString());

        return oldUuidList;
    }

    public static List<Package> getListWithThreePackages() {
        List<Package> list = new ArrayList<>();
        list.add(createPackage1Model());
        list.add(createPackage2Model());
        list.add(createPackage3Model());

        return list;
    }

    public static List<Package> getListWithDeletedPackage() {
        Package deletedPackage = createPackage3Model();
        deletedPackage.setDeleted(true);

        List<Package> packageList = getPackageList();
        packageList.add(deletedPackage);

        return packageList;
    }

    public static List<String> getAllNotDeletedPackageUuids() {
        List<String> list = new ArrayList<>();
        list.add(PACKAGE_1_UUID.toString());
        list.add(PACKAGE_2_UUID.toString());

        return list;
    }

    public static PlaceDto createPlaceDto() {
        PlaceDto dto = new PlaceDto();
        dto.setId(PACKAGE_1_UUID.toString());
        dto.setDescription(DESCRIPTION);
        dto.setPackNumber(PACKAGE1_NUMBER);
        dto.setWeight(11D);
        dto.setHeight(22);
        dto.setLength(33);
        dto.setWidth(44);
        dto.setVolumeWeight(55D);
        dto.setCalcWeight(66D);
        dto.setBarCode(PACKAGE1_ITM_BARCODE);

        return dto;
    }

    public static Package createPackage1ModelForConverter() {
        return Package.builder()
                .packageUuid(PACKAGE_1_UUID.toString())
                .packageNumber(PACKAGE1_NUMBER)
                .barCode(null)
                .itmBarCode(PACKAGE1_BARCODE)
                .deleted(false)
                .build();
    }

    public static OrderDto createOrderDto() {
        MainDto mainDto = new MainDto();
        mainDto.setId(ORDER_UUID.toString());
        mainDto.setOrderNumber(ORDER_NUMBER);
        mainDto.setStatusCode(OrderStatus.STATUS_CREATED);
        mainDto.setOrderType(ORDER_TYPE_ONLINE_SHOP);
        mainDto.setTrueDeliveryModeCode(TARIFF_MODE_DD);
        mainDto.setDeleted(Boolean.FALSE);

        PayerDto payerDto = new PayerDto();
        payerDto.setId(PAYER_UUID.toString());
        payerDto.setType(PayerType.OTHER);

        OtherDto otherDto = new OtherDto();
        ContragentDto contragentDto = new ContragentDto();
        contragentDto.setContractNumber(PAYER_CONTRACT_NUMBER);
        otherDto.setContragent(contragentDto);

        CargoDto cargoDto = new CargoDto();
        cargoDto.setShopSellerName(SELLER_NAME);
        cargoDto.setPlaces(Collections.singletonList(createPlaceDto()));

        OrderDto orderDto = new OrderDto();
        orderDto.setMain(mainDto);
        orderDto.setPayer(payerDto);
        orderDto.setOther(otherDto);
        orderDto.setCargo(cargoDto);
        orderDto.setServices(new ServicesDto());

        return orderDto;
    }

    public static OrderDto createOrderWithAdditionalServicesDto() {
        AdditionalServiceDto additionalServiceDto = new AdditionalServiceDto();
        additionalServiceDto.setCode(AdditionalServiceType.AS_WAREHOUSING_ALIAS);

        AdditionalServiceParamDto param = new AdditionalServiceParamDto();
        param.setValue(String.valueOf(COUNT_DAYS));

        additionalServiceDto.setParams(Collections.singletonList(param));

        OrderDto orderDto = createOrderDto();
        orderDto.getServices().setAdditionalServices(Collections.singletonList(additionalServiceDto));

        return orderDto;
    }

    public static OrderDto createOrderDtoWithPayerTypeSender() {
        OrderDto dto = createOrderDto();
        dto.getPayer().setType(PayerType.SENDER);

        SenderDto senderDto = new SenderDto();
        ContragentDto contragentDto = new ContragentDto();
        senderDto.setContragent(contragentDto);
        senderDto.getContragent().setContractNumber(PAYER_CONTRACT_NUMBER);

        dto.setSender(senderDto);

        return dto;
    }

    public static OrderDto createOrderDtoWithPayerTypeReceiver() {
        OrderDto dto = createOrderDto();
        dto.getPayer().setType(PayerType.RECEIVER);

        ReceiverDto receiverDto = new ReceiverDto();
        ContragentDto contragentDto = new ContragentDto();
        receiverDto.setContragent(contragentDto);
        receiverDto.getContragent().setContractNumber(PAYER_CONTRACT_NUMBER);

        dto.setReceiver(receiverDto);

        return dto;
    }

    public static Order createOrderModelForConverter() {
        Order order = createOrderModel();
        order.setPayerContractUuid(null);
        order.setPayerContractNumber(PAYER_CONTRACT_NUMBER);
        order.setPackages(Collections.singletonList(createPackageModelForOrderDtoConverter()));

        return order;
    }

    public static Order createOrderModelWithoutServicesForConverter() {
        Order order = createOrderModelForConverter();
        order.setCountDay(null);

        return order;
    }

    public static Package createPackageModelForOrderDtoConverter() {
        Package pack = createPackage1ModelForConverter();
        pack.setOrderUuid(ORDER_UUID.toString());
        pack.setTimestamp(TIMESTAMP);

        return pack;
    }

    public static ResponseOrderDto getResponseOrderDto() {
        ResponseOrderDto response = new ResponseOrderDto();
        OrderDto order = new OrderDto();
        response.setOrder(order);

        return response;
    }
}
