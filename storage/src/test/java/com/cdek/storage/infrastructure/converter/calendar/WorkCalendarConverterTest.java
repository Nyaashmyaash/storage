package com.cdek.storage.infrastructure.converter.calendar;

import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.model.calendar.WorkCalendar;
import com.cdek.storage.utils.WorkCalendarTestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WorkCalendarConverterTest.WorkCalendarConverterTestConfiguration.class)
class WorkCalendarConverterTest {

    @Autowired
    UuidConverter uuidConverter;
    @Autowired
    WorkCalendarConverter converter;

    @Test
    void fromDto_ConvertOrderDtoToModel_Success() {
        WorkCalendar actual = converter.fromDto(WorkCalendarTestUtils.crateCalendarEsbDto());
        Assertions.assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder()
                .isEqualTo(WorkCalendarTestUtils.createWorkCalendarModel());
    }

    @Configuration
    static class WorkCalendarConverterTestConfiguration {
        @Bean
        public WorkCalendarConverter converter() {
            return new WorkCalendarConverterImpl();
        }

        @Bean
        public UuidConverter uuidConverter() {
            return new UuidConverterImpl();
        }
    }
}
