package com.cdek.storage.buffer.refresher;

import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.buffer.ports.input.OrderRefresher;
import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.buffer.refresher.helper.CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper;
import com.cdek.storage.buffer.refresher.helper.PackageComparerHelper;
import com.cdek.storage.infrastructure.converter.order.OrderConverter;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class OrderStreamRefresherTest {
    @MockBean
    OrderConverter converter;
    @MockBean
    OrderPsqlRepository orderRepository;
    @MockBean
    PackageBufferRepository packageRepository;
    @MockBean
    PackageComparerHelper packageComparerHelper;
    @MockBean
    CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper compareHelper;
    @MockBean
    CalcStoragePeriodInDays calcStoragePeriodInDays;

    OrderRefresher refresher;

    @BeforeEach
    void before() {
        refresher = new OrderStreamRefresher(converter, orderRepository, packageRepository, packageComparerHelper,
                compareHelper, calcStoragePeriodInDays);
        Mockito.clearInvocations(converter, orderRepository, packageRepository, packageComparerHelper, compareHelper,
                calcStoragePeriodInDays);
    }

    @Test
    void refreshIfNeeded_NewOrder_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OrderTestUtils.createOrderModel());
        Mockito.doNothing().when(orderRepository).saveNewOrder(Mockito.any());
        Mockito.doNothing().when(packageRepository).saveOrUpdatePackage(Mockito.any());
        Mockito.doNothing().when(compareHelper)
                .compareOrdersFieldsAndCalcStoragePeriod(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(calcStoragePeriodInDays).calcStoragePeriod(Mockito.any());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.times(1)).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.times(2)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.times(1)).calcStoragePeriod(Mockito.any());
    }

    @Test
    void refreshIfNeeded_DeletedOrder_Delete() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OrderTestUtils.createDeletedOrderModel());
        Mockito.doNothing().when(orderRepository).deleteOrderAndPackages(Mockito.any());
        Mockito.doNothing().when(compareHelper)
                .compareOrdersFieldsAndCalcStoragePeriod(Mockito.any(), Mockito.any());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.times(1)).deleteOrderAndPackages(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.never()).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(packageComparerHelper, Mockito.never()).comparePackages(Mockito.anyList(), Mockito.anyList());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }

    @Test
    void refreshIfNeeded_OrderInStatusCreated_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OrderTestUtils.createOrderModel());
        Mockito.doNothing().when(orderRepository).updateOrder(Mockito.any());
        Mockito.doNothing().when(packageComparerHelper).comparePackages(Mockito.anyList(), Mockito.anyList());
        Mockito.doNothing().when(compareHelper)
                .compareOrdersFieldsAndCalcStoragePeriod(Mockito.any(), Mockito.any());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).deleteOrderAndPackages(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.times(1)).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.never()).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(packageComparerHelper, Mockito.times(1)).comparePackages(Mockito.anyList(), Mockito.anyList());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }

    @Test
    void refreshIfNeeded_NewOrderWithTariffModeTT_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OrderTestUtils.createOrderWithTrueDeliveryModeTT());
        Mockito.doNothing().when(orderRepository).saveNewOrder(Mockito.any());
        Mockito.doNothing().when(packageRepository).saveOrUpdatePackage(Mockito.any());
        Mockito.doNothing().when(compareHelper)
                .compareOrdersFieldsAndCalcStoragePeriod(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(calcStoragePeriodInDays).calcStoragePeriod(Mockito.any());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.times(1)).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.times(2)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.times(1)).calcStoragePeriod(Mockito.any());
    }

    @Test
    void refreshIfNeeded_NewOrderWithoutUuidAndNumberOfContract_Skip() {
        Mockito.when(converter.fromDto(Mockito.any()))
                .thenReturn(OrderTestUtils.createOrderWithoutUuidAndNumberOfContract());
        Mockito.doNothing().when(compareHelper)
                .compareOrdersFieldsAndCalcStoragePeriod(Mockito.any(), Mockito.any());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.never()).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }

    @Test
    void refreshIfNeeded_NewOrderIsDeleted_Skip() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OrderTestUtils.createDeletedOrderModel());

        refresher.refreshIfNeeded(OrderTestUtils.createOrderStreamDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).saveNewOrder(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).updateOrder(Mockito.any());
        Mockito.verify(packageRepository, Mockito.never()).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
    }
}
