package com.cdek.storage.infrastructure.converter.logistic;

import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.model.logistic.LogisticCity;
import com.cdek.storage.utils.LogisticCityTestUtils;
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
@ContextConfiguration(classes = LogisticCityConverterTest.LogisticCityConverterTestConfiguration.class)
class LogisticCityConverterTest {

    @Autowired
    LogisticCityConverter converter;

    @Test
    void fromDto_ConvertContractDtoToModel_Success() {
        LogisticCity actual = converter.fromDto(LogisticCityTestUtils.createCityEsbDto());
        Assertions.assertThat(actual).isEqualTo(LogisticCityTestUtils.createLogisticCityModel());
    }

    @Configuration
    static class LogisticCityConverterTestConfiguration {
        @Bean
        public LogisticCityConverter converter() {
            return new LogisticCityConverterImpl();
        }

        @Bean
        public UuidConverter uuidConverter() {
            return new UuidConverterImpl();
        }
    }
}
