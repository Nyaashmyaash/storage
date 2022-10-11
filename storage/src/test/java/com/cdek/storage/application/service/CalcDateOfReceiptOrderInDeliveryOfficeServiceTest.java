package com.cdek.storage.application.service;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.input.CalcDateOfReceiptOrderInDeliveryOffice;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.output.PackageRepository;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.utils.CargoPlaceStatusTestUtils;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
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
class CalcDateOfReceiptOrderInDeliveryOfficeServiceTest {

    @MockBean
    PackageRepository packageRepository;
    @MockBean
    CalcLastStorageDate calcLastStorageDate;
    @MockBean
    CalcHelper calcHelper;

    CalcDateOfReceiptOrderInDeliveryOffice calcDateOfReceipt;

    @BeforeEach
    void before() {
        calcDateOfReceipt = new CalcDateOfReceiptOrderInDeliveryOfficeService(packageRepository, calcLastStorageDate,
                calcHelper);
        Mockito.clearInvocations(packageRepository, calcLastStorageDate, calcHelper);
    }

    @Test
    void calcDateOfReceiptOrder_CargoPlaceStatus_NotSuccessful() {
        Mockito.when(packageRepository.getNotDeletedPackagesByOrderUuid(Mockito.anyString()))
                .thenReturn(Collections.singletonList(OrderTestUtils.createPackage1Model()));
        Mockito.when(calcHelper.isAllStatusesOfPackagesCorrect(Mockito.any(), Mockito.anyList())).thenReturn(false);

        calcDateOfReceipt.calcDateOfReceiptOrder(OrderTestUtils.ORDER_UUID.toString(), TariffMode.TARIFF_MODE_DD,
                OrderStorageTestUtils.createOrderStorage());

        Mockito.verify(calcHelper, Mockito.never()).getMaxDateTimeChangeStatus(Mockito.anyList());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void calcDateOfReceiptOrder_CargoPlaceStatus_Success() {
        Mockito.when(packageRepository.getNotDeletedPackagesByOrderUuid(Mockito.anyString()))
                .thenReturn(Collections.singletonList(OrderTestUtils.createPackage1Model()));
        Mockito.when(calcHelper.isAllStatusesOfPackagesCorrect(Mockito.any(), Mockito.anyList()))
                .thenReturn(true);
        Mockito.when(calcHelper.getMaxDateTimeChangeStatus(Mockito.anyList()))
                .thenReturn(CargoPlaceStatusTestUtils.TIMESTAMP);
        Mockito.doNothing().when(calcLastStorageDate).calcLastStorageDate(Mockito.any());

        calcDateOfReceipt.calcDateOfReceiptOrder(OrderTestUtils.ORDER_UUID.toString(), TariffMode.TARIFF_MODE_DD,
                OrderStorageTestUtils.createOrderStorage());

        Mockito.verify(calcHelper, Mockito.times(1)).getMaxDateTimeChangeStatus(Mockito.anyList());
        Mockito.verify(calcLastStorageDate, Mockito.times(1)).calcLastStorageDate(Mockito.any());
    }
}
