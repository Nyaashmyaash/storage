package com.cdek.storage.application.service;

import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CalcStoragePeriodInDaysServiceTest {

    @MockBean
    OrderStorageRepository orderStorageRepository;
    @MockBean
    CalcHelper calcHelper;
    @MockBean
    OrderStorageEventPublisher eventService;
    @MockBean
    CalcLastStorageDate calcLastStorageDate;

    CalcStoragePeriodInDays calcLastStoragePeriodService;

    @BeforeEach
    void before() {
        calcLastStoragePeriodService = new CalcStoragePeriodInDaysService(orderStorageRepository, calcHelper, eventService, calcLastStorageDate);
        Mockito.clearInvocations(orderStorageRepository, calcHelper, eventService, calcLastStorageDate);
    }

    @Test
    void calcStoragePeriod_Order_DontUpdateNewOrderStorage() {
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(10);
        Mockito.when(orderStorageRepository.isOrderStorageExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(orderStorageRepository.getOrderStorageByOrderUuid(Mockito.anyString()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());

        calcLastStoragePeriodService.calcStoragePeriod(OrderTestUtils.createOrderModel());

        Mockito.verify(orderStorageRepository, Mockito.never()).updateOrderStorage(Mockito.any());
        Mockito.verify(eventService, Mockito.never()).publish(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void calcStoragePeriod_Order_UpdateOrderStorageWithDateReceipt() {
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(5);
        Mockito.when(orderStorageRepository.isOrderStorageExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(orderStorageRepository.getOrderStorageByOrderUuid(Mockito.anyString()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());

        calcLastStoragePeriodService.calcStoragePeriod(OrderTestUtils.createOrderModel());

        Mockito.verify(orderStorageRepository, Mockito.times(1)).updateOrderStorage(Mockito.any());
        Mockito.verify(eventService, Mockito.never()).publish(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.times(1)).calcLastStorageDate(Mockito.any());
    }

    @Test
    void calcStoragePeriod_Order_UpdateOrderStorageWithoutDateReceipt() {
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(5);
        Mockito.when(orderStorageRepository.isOrderStorageExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(orderStorageRepository.getOrderStorageByOrderUuid(Mockito.anyString()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());

        calcLastStoragePeriodService.calcStoragePeriod(OrderTestUtils.createOrderModel());

        Mockito.verify(orderStorageRepository, Mockito.times(1)).updateOrderStorage(Mockito.any());
        Mockito.verify(eventService, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void calcStoragePeriod_Order_SaveNewOrderStorage() {
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(5);
        Mockito.when(orderStorageRepository.isOrderStorageExists(Mockito.anyString())).thenReturn(false);

        calcLastStoragePeriodService.calcStoragePeriod(OrderTestUtils.createOrderModel());

        Mockito.verify(orderStorageRepository, Mockito.never()).updateOrderStorage(Mockito.any());
        Mockito.verify(orderStorageRepository, Mockito.times(1)).saveNewOrderStorage(Mockito.any());
        Mockito.verify(eventService, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void calcStoragePeriod_OrderWithDeliveryModeTT_SaveNewOrderStorage() {
        calcLastStoragePeriodService.calcStoragePeriod(OrderTestUtils.createOrderWithTrueDeliveryModeTT());

        Mockito.verify(orderStorageRepository, Mockito.never()).isOrderStorageExists(Mockito.anyString());
        Mockito.verify(orderStorageRepository, Mockito.never()).getOrderStorageByOrderUuid(Mockito.anyString());
        Mockito.verify(orderStorageRepository, Mockito.never()).updateOrderStorage(Mockito.any());
        Mockito.verify(orderStorageRepository, Mockito.never()).saveNewOrderStorage(Mockito.any());
        Mockito.verify(eventService, Mockito.never()).publish(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }
}
