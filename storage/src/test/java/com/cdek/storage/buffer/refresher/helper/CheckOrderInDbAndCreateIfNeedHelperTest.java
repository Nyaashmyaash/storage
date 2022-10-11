package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.buffer.ports.output.OrderBufferRepository;
import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.infrastructure.converter.order.OrderDtoConverter;
import com.cdek.storage.infrastructure.security.provider.UserProvider;
import com.cdek.storage.infrastructure.service.OrderClient;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CheckOrderInDbAndCreateIfNeedHelperTest {

    @MockBean
    OrderDtoConverter orderDtoConverter;
    @MockBean
    OrderRepository orderRepository;
    @MockBean
    OrderBufferRepository orderBufferRepository;
    @MockBean
    PackageBufferRepository packageBufferRepository;
    @MockBean
    OrderClient orderClient;
    @MockBean
    CalcStoragePeriodInDays calcStoragePeriodInDays;
    @MockBean
    UserProvider userProvider;

    CheckOrderInDbAndCreateIfNeedHelper helper;

    @BeforeEach
    void before() {
        helper = new CheckOrderInDbAndCreateIfNeedHelper(orderDtoConverter, orderRepository, orderBufferRepository,
                packageBufferRepository, orderClient, calcStoragePeriodInDays, userProvider);
        Mockito.clearInvocations(orderDtoConverter, orderRepository, orderBufferRepository, packageBufferRepository,
                orderClient, calcStoragePeriodInDays, userProvider);
    }

    @Test
    void checkOrderInDbAndCreateIfNeed_ExistOrderUuid_Success() {
        Mockito.when(orderRepository.isOrderExists(Mockito.anyString())).thenReturn(true);

        helper.checkOrderInDbAndCreateIfNeed(OrderTestUtils.ORDER_UUID.toString());

        Mockito.verify(userProvider, Mockito.never()).getToken();
        Mockito.verify(orderClient, Mockito.never()).getOrderByUuid(Mockito.any());
        Mockito.verify(orderDtoConverter, Mockito.never()).fromOrderDto(Mockito.any());
        Mockito.verify(orderBufferRepository, Mockito.never()).saveNewOrder(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.never()).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }

    @Test
    void checkOrderInDbAndCreateIfNeed_NotExistOrderUuid_Success() {
        Mockito.when(orderRepository.isOrderExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(userProvider.getToken()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(orderClient.getOrderByUuid(Mockito.any())).thenReturn(OrderTestUtils.getResponseOrderDto());
        Mockito.when(orderDtoConverter.fromOrderDto(Mockito.any()))
                .thenReturn(OrderTestUtils.createOrderModel());

        helper.checkOrderInDbAndCreateIfNeed(OrderTestUtils.ORDER_UUID.toString());

        Mockito.verify(userProvider, Mockito.times(1)).getToken();
        Mockito.verify(orderClient, Mockito.times(1)).getOrderByUuid(Mockito.any());
        Mockito.verify(orderDtoConverter, Mockito.times(1)).fromOrderDto(Mockito.any());
        Mockito.verify(orderBufferRepository, Mockito.times(1)).saveNewOrder(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.times(2)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.times(1)).calcStoragePeriod(Mockito.any());
    }

    @Test
    void checkOrderInDbAndCreateIfNeed_NotExistOrderUuidWithTariffModeTT_Success() {
        Mockito.when(orderRepository.isOrderExists(Mockito.anyString())).thenReturn(false);

        Mockito.when(userProvider.getToken()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(orderClient.getOrderByUuid(Mockito.any())).thenReturn(OrderTestUtils.getResponseOrderDto());
        Mockito.when(orderDtoConverter.fromOrderDto(Mockito.any()))
                .thenReturn(OrderTestUtils.createOrderWithTrueDeliveryModeTT());

        helper.checkOrderInDbAndCreateIfNeed(OrderTestUtils.ORDER_UUID.toString());

        Mockito.verify(userProvider, Mockito.times(1)).getToken();
        Mockito.verify(orderClient, Mockito.times(1)).getOrderByUuid(Mockito.any());
        Mockito.verify(orderDtoConverter, Mockito.times(1)).fromOrderDto(Mockito.any());
        Mockito.verify(orderBufferRepository, Mockito.times(1)).saveNewOrder(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.times(2)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }
}
