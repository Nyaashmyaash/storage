package com.cdek.storage.application.service.helper;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.catalog.common.entity.OrderType;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.output.OrderCargoPlaceStatusRepository;
import com.cdek.storage.model.order.Package;
import com.cdek.storage.utils.CargoPlaceStatusTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CalcHelperTest {

    @MockBean
    OrderCargoPlaceStatusRepository placeStatusRepository;
    @MockBean
    CalcIndividualConditionsHelper calcIndividualConditionsHelper;

    CalcHelper calcHelper;

    @BeforeEach
    void before() {
        calcHelper = new CalcHelper(placeStatusRepository, calcIndividualConditionsHelper);
        Mockito.clearInvocations(placeStatusRepository, calcIndividualConditionsHelper);
    }

    @Test
    void calcCountDay_OrderWithTariffModeToPostamat_returnCountDay() {
        int actual = calcHelper
                .calcCountDay(OrderTestUtils.getOrder(TariffMode.TARIFF_MODE_DP, null, OrderType.ORDER_TYPE_DELIVERY));
        Assertions.assertEquals(3, actual);

        Mockito.when(calcIndividualConditionsHelper.getIndividualConditionsForFreeStoragePeriod(Mockito.any()))
                .thenReturn(9);
        int actual2 = calcHelper
                .calcCountDay(OrderTestUtils.getOrder(TariffMode.TARIFF_MODE_DP, null, OrderType.ORDER_TYPE_ONLINE_SHOP));
        Assertions.assertEquals(9, actual2);
    }

    @Test
    void calcCountDay_OrderWithTariffModeToDoorOrToWarehouse_returnCountDay() {
        int actual = calcHelper
                .calcCountDay(OrderTestUtils.getOrder(TariffMode.TARIFF_MODE_WW, null, OrderType.ORDER_TYPE_DELIVERY));
        Assertions.assertEquals(7, actual);

        Mockito.when(calcIndividualConditionsHelper.getIndividualConditionsForFreeStoragePeriod(Mockito.any()))
                .thenReturn(9);
        int actual2 = calcHelper
                .calcCountDay(OrderTestUtils.getOrder(TariffMode.TARIFF_MODE_DD, 3, OrderType.ORDER_TYPE_ONLINE_SHOP));
        Assertions.assertEquals(12, actual2);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeDP_returnTrue() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_DP, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeDP_returnFalse() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.CREATED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_WP, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeDD_returnTrue() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils
                        .createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.ACCEPTED_AT_DELIVERY_WAREHOUSE));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_DD, packages);

        Assertions.assertTrue(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeWD_returnFalse() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.CREATED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_WD, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeWW_returnTrue() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils
                        .createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.ACCEPTED_AT_WAREHOUSE_ON_DEMAND));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_WW, packages);

        Assertions.assertTrue(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeDW_returnFalse() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.CREATED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_DW, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModeTT_returnFalse() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.CREATED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_TT, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void isAllStatusesCorrect_OrderWithTariffModePP_returnFalse() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());

        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils.createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED));

        boolean actual = calcHelper.isAllStatusesOfPackagesCorrect(TariffMode.TARIFF_MODE_PP, packages);

        Assertions.assertFalse(actual);
    }

    @Test
    void getMaxDateTimeChangeStatus_PackageList_ReturnCorrectInstant() {
        List<Package> packages = Collections.singletonList(OrderTestUtils.createPackage1Model());
        Mockito.when(placeStatusRepository.findCurrentStatusByPackageUuid(Mockito.anyString())).thenReturn(
                CargoPlaceStatusTestUtils
                        .createCargoPlaceStatusModel(OrderCargoPlaceLogisticStatus.ACCEPTED_AT_WAREHOUSE_ON_DEMAND));

        Instant actual = calcHelper.getMaxDateTimeChangeStatus(packages);

        Assertions.assertEquals(CargoPlaceStatusTestUtils.TIMESTAMP, actual);
    }
}
