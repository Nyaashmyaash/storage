package com.cdek.storage.buffer.refresher.helper;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
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
class CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelperTest {

    @MockBean
    OrderStoragePsqlRepository storagePsqlRepository;
    @MockBean
    CalcLastStorageDate calcLastStorageDate;
    @MockBean
    CalcHelper calcHelper;
    @MockBean
    OrderStorageEventPublisher eventPublisher;

    CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper compareHelper;

    @BeforeEach
    void before() {
        compareHelper = new CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper(storagePsqlRepository,
                calcLastStorageDate, calcHelper, eventPublisher);
        Mockito.clearInvocations(storagePsqlRepository, calcLastStorageDate, calcHelper, eventPublisher);
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldAndNewCountDayNotEquals() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModelWithCountDay(20),
                OrderTestUtils.createOrderModel());

        Mockito.verify(calcHelper, Mockito.times(1)).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldAndNewCountDayEquals() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModel(),
                OrderTestUtils.createOrderModel());

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldCountDayIsNull() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModel(),
                OrderTestUtils.createOrderModelWithoutServices());

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.times(1)).calcLastStorageDate(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_NewCountDayIsNull() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper
                .compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModelWithoutServices(),
                        OrderTestUtils.createOrderModel());

        Mockito.verify(calcHelper, Mockito.times(1)).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldAndNewCountDayIsNull() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper
                .compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModelWithoutServices(),
                        OrderTestUtils.createOrderModelWithoutServices());

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldCountDayMoreThanNewCountDay() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(OrderTestUtils.createOrderModel(),
                OrderTestUtils.createOrderModelWithCountDay(20));

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldAndNewTariffIsPostamat() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DP),
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DP));

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldTariffIsPostamat() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DD),
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DP));

        Mockito.verify(calcHelper, Mockito.times(1)).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldAndNewTariffIsNotPostamat() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DW),
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DD));

        Mockito.verify(calcHelper, Mockito.never()).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_NewTariffIsPostamat() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DP),
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DD));

        Mockito.verify(calcHelper, Mockito.times(1)).calcCountDay(Mockito.any());
    }

    @Test
    void compareOrdersFieldsAndCalcStoragePeriod_OldTariffIsPostamatAndDateOfReceiptExist() {
        Mockito.doNothing().when(eventPublisher).publish(Mockito.any());
        Mockito.when(storagePsqlRepository.getOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.when(calcHelper.calcCountDay(Mockito.any())).thenReturn(1);

        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DD),
                OrderTestUtils.createOrderModelByTariffMode(TariffMode.TARIFF_MODE_DP));

        Mockito.verify(calcHelper, Mockito.times(1)).calcCountDay(Mockito.any());
    }
}
