package com.cdek.storage.buffer.refresher;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.cargoplacelogisticstatus.common.dto.OrderCargoPlaceDto;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.input.CalcDateOfReceiptOrderInDeliveryOffice;
import com.cdek.storage.buffer.refresher.helper.CalcAndGetOrderStorageHelper;
import com.cdek.storage.buffer.refresher.helper.CheckOrderInDbAndCreateIfNeedHelper;
import com.cdek.storage.infrastructure.converter.order.CargoPlaceStatusConverter;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderCargoPlaceStatusPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.utils.OrderCargoPlaceTestUtils;
import com.cdek.storage.utils.OrderStorageTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class OrderCargoPlaceStreamRefresherTest {
    @MockBean
    CargoPlaceStatusConverter converter;
    @MockBean
    OrderCargoPlaceStatusPsqlRepository orderCargoPlaceRepository;
    @MockBean
    CalcDateOfReceiptOrderInDeliveryOffice calcDateOfReceiptOrder;
    @MockBean
    CheckOrderInDbAndCreateIfNeedHelper checkOrderHelper;
    @MockBean
    OrderPsqlRepository orderPsqlRepository;
    @MockBean
    CalcAndGetOrderStorageHelper calcAndGetOrderStorage;

    OrderCargoPlaceStatusStreamRefresher refresher;

    @BeforeEach
    void before() {
        refresher = new OrderCargoPlaceStatusStreamRefresher(converter, orderCargoPlaceRepository,
                calcDateOfReceiptOrder, checkOrderHelper, orderPsqlRepository, calcAndGetOrderStorage);
        refresher.init();

        Mockito.clearInvocations(converter, orderCargoPlaceRepository, calcDateOfReceiptOrder, checkOrderHelper,
                orderPsqlRepository, calcAndGetOrderStorage);
    }

    @Test
    void saveIfNeeded_NewCargoPlaceStatus_SaveAndNotCalcDateOfReceipt() {
        Mockito.doNothing().when(checkOrderHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(orderPsqlRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_DP);
        Mockito.when(converter.fromDto((OrderCargoPlaceDto) Mockito.any()))
                .thenReturn(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
        Mockito.doNothing().when(orderCargoPlaceRepository).saveNewStatus(Mockito.any());
        Mockito.when(orderCargoPlaceRepository
                .getAllStatusListByPackageUuidAndStatus(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(OrderCargoPlaceTestUtils.getStatusList());

        refresher.saveIfNeeded(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto((OrderCargoPlaceDto) Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.times(1)).saveNewStatus(Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.never()).updateStatus(Mockito.any());
        Mockito.verify(calcDateOfReceiptOrder, Mockito.never()).calcDateOfReceiptOrder(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(calcAndGetOrderStorage, Mockito.never()).getOrderStoragePeriod(Mockito.anyString());
    }

    @Test
    void saveIfNeeded_NewCargoPlaceStatus_SaveAndCalcDateOfReceipt() {
        Mockito.doNothing().when(checkOrderHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(orderPsqlRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_DD);
        Mockito.when(converter.fromDto((OrderCargoPlaceDto) Mockito.any()))
                .thenReturn(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
        Mockito.doNothing().when(orderCargoPlaceRepository).saveNewStatus(Mockito.any());
        Mockito.when(calcAndGetOrderStorage.getOrderStoragePeriod(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.when(orderCargoPlaceRepository
                .getAllStatusListByPackageUuidAndStatus(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED.name()));

        refresher.saveIfNeeded(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto((OrderCargoPlaceDto) Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.times(1)).saveNewStatus(Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.never()).updateStatus(Mockito.any());
        Mockito.verify(calcDateOfReceiptOrder, Mockito.times(1))
                .calcDateOfReceiptOrder(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(calcAndGetOrderStorage, Mockito.times(1)).getOrderStoragePeriod(Mockito.anyString());
    }

    @Test
    void saveIfNeeded_UpdatedCargoPlaceStatus_Update() {
        Mockito.doNothing().when(checkOrderHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(orderPsqlRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_DP);
        Mockito.when(converter.fromDto((OrderCargoPlaceDto) Mockito.any()))
                .thenReturn(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
        Mockito.doNothing().when(orderCargoPlaceRepository).saveNewStatus(Mockito.any());
        Mockito.when(orderCargoPlaceRepository
                .getAllStatusListByPackageUuidAndStatus(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED.name()));

        refresher.saveIfNeeded(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent(),
                OrderCargoPlaceTestUtils.TIMESTAMP);

        Mockito.verify(converter, Mockito.times(1)).fromDto((OrderCargoPlaceDto) Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.never()).saveNewStatus(Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.times(1)).updateStatus(Mockito.any());
        Mockito.verify(calcDateOfReceiptOrder, Mockito.never())
                .calcDateOfReceiptOrder(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(calcAndGetOrderStorage, Mockito.never()).getOrderStoragePeriod(Mockito.anyString());
    }

    @Test
    void saveIfNeeded_NewCargoPlaceStatusNotFinalStatus_Skip() {
        Mockito.doNothing().when(checkOrderHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());

        refresher.saveIfNeeded(OrderCargoPlaceTestUtils.createOrderCargoPlaceNotFinalStatusEvent(), null);

        Mockito.verify(converter, Mockito.never()).fromDto((OrderCargoPlaceDto) Mockito.any());
        Mockito.verify(orderPsqlRepository, Mockito.never()).getTrueDeliveryModeCodeByOrderUuid(Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.never()).saveNewStatus(Mockito.any());
        Mockito.verify(calcDateOfReceiptOrder, Mockito.never())
                .calcDateOfReceiptOrder(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(calcAndGetOrderStorage, Mockito.never()).getOrderStoragePeriod(Mockito.anyString());
    }

    @Test
    void saveIfNeeded_NewCargoPlaceStatusWrongTariffMode_Skip() {
        Mockito.doNothing().when(checkOrderHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(converter.fromDto((OrderCargoPlaceDto) Mockito.any()))
                .thenReturn(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
        Mockito.when(orderPsqlRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_TT);

        refresher.saveIfNeeded(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent(), null);

        Mockito.verify(converter, Mockito.never()).fromDto((OrderCargoPlaceDto) Mockito.any());
        Mockito.verify(orderCargoPlaceRepository, Mockito.never()).saveNewStatus(Mockito.any());
        Mockito.verify(calcDateOfReceiptOrder, Mockito.never())
                .calcDateOfReceiptOrder(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(calcAndGetOrderStorage, Mockito.never()).getOrderStoragePeriod(Mockito.anyString());
    }
}
