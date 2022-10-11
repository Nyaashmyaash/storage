package com.cdek.storage.application.service.helper;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.output.OrderCargoPlaceStatusRepository;
import com.cdek.storage.application.ports.output.WorkCalendarDayRepository;
import com.cdek.storage.application.ports.output.WorkCalendarRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.LogisticCityPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OfficePsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.utils.LogisticCityTestUtils;
import com.cdek.storage.utils.OfficeTestUtils;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.WorkCalendarTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CalcDeadlineHelperTest {

    @MockBean
    WorkCalendarRepository workCalendarRepository;
    @MockBean
    WorkCalendarDayRepository workCalendarDayRepository;
    @MockBean
    OrderCargoPlaceStatusRepository placeStatusRepository;
    @MockBean
    OfficePsqlRepository officeRepository;
    @MockBean
    LogisticCityPsqlRepository logisticCityRepository;
    @MockBean
    OrderPsqlRepository orderRepository;

    CalcDeadlineHelper helper;

    @BeforeEach
    void before() {
        helper = new CalcDeadlineHelper(workCalendarRepository, workCalendarDayRepository, placeStatusRepository,
                officeRepository, logisticCityRepository, orderRepository);
        Mockito.clearInvocations(workCalendarRepository, workCalendarDayRepository, placeStatusRepository,
                officeRepository, logisticCityRepository, orderRepository);
    }

    @Test
    void calcDeadlineOfStoragePeriod_TariffModePP_returnValidDate() {
        Mockito.when(orderRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_PP);

        Instant actual = helper.calcDeadlineOfStoragePeriod(OrderStorageTestUtils.createOrderStorage());
        Instant exp = OrderStorageTestUtils.DATE_OF_RECEIPT
                .plus(OrderStorageTestUtils.SHEL_LIFE_ORDER_IN_DAYS, ChronoUnit.DAYS);
        Assertions.assertEquals(exp, actual);
    }

    @Test
    void calcDeadlineOfStoragePeriod_OrderStorageWithRegion_returnValidDate() {
        Mockito.when(orderRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_DD);
        Mockito.when(placeStatusRepository.getCurrentOfficeUuidByOrderUuid(Mockito.anyString()))
                .thenReturn(OfficeTestUtils.OFFICE_UUID.toString());
        Mockito.when(officeRepository.getOfficeByUuid(Mockito.anyString()))
                .thenReturn(OfficeTestUtils.createOfficeModel());
        Mockito.when(logisticCityRepository.getLogisticCity(Mockito.anyString()))
                .thenReturn(LogisticCityTestUtils.createLogisticCityModel());
        Mockito.when(workCalendarRepository.isWorkCalendarExists(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(true);
        Mockito.when(workCalendarDayRepository
                .getDateInformation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(WorkCalendarTestUtils.createNotHolidayDate());

        Instant actual = helper.calcDeadlineOfStoragePeriod(OrderStorageTestUtils.createOrderStorage());
        Instant exp = LocalDate.now().plusDays(5).atTime(LocalTime.MAX).atZone(LogisticCityTestUtils.TIME_ZONE_ID).toInstant()
                .truncatedTo(ChronoUnit.SECONDS);
        Assertions.assertEquals(exp, actual);
    }

    @Test
    void calcDeadlineOfStoragePeriod_OrderStorageWithoutRegion_returnValidDate() {
        Mockito.when(orderRepository.getTrueDeliveryModeCodeByOrderUuid(Mockito.anyString()))
                .thenReturn(TariffMode.TARIFF_MODE_DD);
        Mockito.when(placeStatusRepository.getCurrentOfficeUuidByOrderUuid(Mockito.anyString()))
                .thenReturn(OfficeTestUtils.OFFICE_UUID.toString());
        Mockito.when(officeRepository.getOfficeByUuid(Mockito.anyString()))
                .thenReturn(OfficeTestUtils.createOfficeModel());
        Mockito.when(logisticCityRepository.getLogisticCity(Mockito.anyString()))
                .thenReturn(LogisticCityTestUtils.createLogisticCityModel());
        Mockito.when(workCalendarRepository.isWorkCalendarExists(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(false);
        Mockito.when(workCalendarDayRepository
                .getDateInformation(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenReturn(WorkCalendarTestUtils.createNotHolidayDate());

        Instant actual = helper.calcDeadlineOfStoragePeriod(OrderStorageTestUtils.createOrderStorage());
        Instant exp = LocalDate.now().plusDays(5).atTime(LocalTime.MAX).atZone(LogisticCityTestUtils.TIME_ZONE_ID).toInstant()
                .truncatedTo(ChronoUnit.SECONDS);
        Assertions.assertEquals(exp, actual);
    }
}
