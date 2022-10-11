package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.calendar.WorkCalendarDay;
import com.cdek.storage.utils.WorkCalendarTestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { WorkCalendarDayPsqlRepository.class, WorkCalendarPsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class WorkCalendarDayPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    WorkCalendarDayPsqlRepository workCalendarDayRepository;
    @Autowired
    WorkCalendarPsqlRepository workCalendarRepository;

    @BeforeEach
    void saveNewWorkCalendarDayInDb() {
        workCalendarRepository.saveNewWorkCalendar(WorkCalendarTestUtils.createWorkCalendarWithRegion());
        workCalendarRepository.saveNewWorkCalendar(WorkCalendarTestUtils.createWorkCalendarWithoutRegion());
        workCalendarDayRepository.saveNewCalendarDays(WorkCalendarTestUtils.getDayListForCalendarWithRegion());
        workCalendarDayRepository.saveNewCalendarDays(WorkCalendarTestUtils.getDayListForCalendarWithoutRegion());
    }

    @Test
    void findWorkCalendarDayByCalendarUuidAndDate_ValidModel_Success() {
        WorkCalendarDay actual = workCalendarDayRepository.findWorkCalendarDayByCalendarUuidAndDate(
                WorkCalendarTestUtils.CALENDAR_WITH_REGION.toString(),
                WorkCalendarTestUtils.DATE_4.toString()
        );

        Assertions.assertThat(actual).isEqualTo(WorkCalendarTestUtils.createWorkCalendarForRegionDay4());
    }

    @Test
    void updateWorkCalendarDay_ValidUpdatedModel_Success() {
        workCalendarDayRepository
                .updateCalendarDays(Collections.singletonList(WorkCalendarTestUtils.getUpdatedWorkCalendarDay4()));
        WorkCalendarDay actual = workCalendarDayRepository.findWorkCalendarDayByCalendarUuidAndDate(
                WorkCalendarTestUtils.CALENDAR_WITH_REGION.toString(),
                WorkCalendarTestUtils.DATE_4.toString()
        );

        Assertions.assertThat(actual).isEqualTo(WorkCalendarTestUtils.getUpdatedWorkCalendarDay4());
    }

    @Test
    void getLastDateStoragePeriod_ValidData_Success() {
        WorkCalendarDay actual1 = workCalendarDayRepository.getDateInformation(
                WorkCalendarTestUtils.COUNTRY_UUID.toString(),
                WorkCalendarTestUtils.REGION_UUID.toString(),
                WorkCalendarTestUtils.DATE_4.toString()
        );
        Assertions.assertThat(actual1).isEqualTo(WorkCalendarTestUtils.createWorkCalendarForRegionDay4());

        WorkCalendarDay actual2 = workCalendarDayRepository.getDateInformation(
                WorkCalendarTestUtils.COUNTRY_UUID.toString(),
                null,
                WorkCalendarTestUtils.DATE_4.toString()
        );
        Assertions.assertThat(actual2).isEqualTo(WorkCalendarTestUtils.createWorkCalendarForCountryDay4());
    }
}
