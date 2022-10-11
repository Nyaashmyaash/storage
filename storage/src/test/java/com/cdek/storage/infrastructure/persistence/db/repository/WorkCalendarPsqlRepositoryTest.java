package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.calendar.WorkCalendar;
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

import java.time.Instant;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { WorkCalendarPsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class WorkCalendarPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    WorkCalendarPsqlRepository workCalendarRepository;

    @BeforeEach
    void saveNewWorkCalendarInDb() {
        workCalendarRepository.saveNewWorkCalendar(WorkCalendarTestUtils.createWorkCalendarModel());
    }

    @Test
    void findWorkCalendarByUuid_ValidModel_Success() {
        WorkCalendar actual = workCalendarRepository.findWorkCalendarByUuid(WorkCalendarTestUtils.CALENDAR_WITH_REGION.toString());

        Assertions.assertThat(actual).isEqualTo(WorkCalendarTestUtils.createWorkCalendarModelForDb());
    }

    @Test
    void updateWorkCalendar_ValidUpdatedModel_Success() {
        workCalendarRepository.updateWorkCalendar(WorkCalendarTestUtils.createUpdatedWorkCalendarModelForDb());
        WorkCalendar actual = workCalendarRepository.findWorkCalendarByUuid(WorkCalendarTestUtils.CALENDAR_WITH_REGION.toString());

        Assertions.assertThat(actual).isEqualTo(WorkCalendarTestUtils.createUpdatedWorkCalendarModelForDb());
    }

    @Test
    void getTimestamp_ValidCalendarUuid_Success() {
        Instant actual = workCalendarRepository.getTimestamp(WorkCalendarTestUtils.CALENDAR_WITH_REGION.toString());

        Assertions.assertThat(actual).isEqualTo(WorkCalendarTestUtils.TIMESTAMP);
    }

    @Test
    void isWorkCalendarExists_RegionUuidAndYear_Success() {
        boolean actual = workCalendarRepository
                .isWorkCalendarExists(WorkCalendarTestUtils.REGION_UUID.toString(), WorkCalendarTestUtils.YEAR);

        Assertions.assertThat(actual).isTrue();
    }
}
