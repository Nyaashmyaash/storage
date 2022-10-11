package com.cdek.storage.infrastructure.converter.office;

import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.InstantConverterImpl;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.model.office.Office;
import com.cdek.storage.utils.OfficeTestUtils;
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
@ContextConfiguration(classes = OfficeConverterTest.OfficeConverterTestConfiguration.class)
class OfficeConverterTest {

    @Autowired
    OfficeConverter converter;

    @Test
    void fromDto_ConvertContractDtoToModel_Success() {
        Office actual = converter.fromDto(OfficeTestUtils.createOfficeEsbEventDto());
        Assertions.assertThat(actual).isEqualTo(OfficeTestUtils.createOfficeModel());
    }

    @Configuration
    static class OfficeConverterTestConfiguration {
        @Bean
        public OfficeConverter converter() {
            return new OfficeConverterImpl();
        }

        @Bean
        public UuidConverter uuidConverter() {
            return new UuidConverterImpl();
        }

        @Bean
        public InstantConverter instantConverter() {
            return new InstantConverterImpl();
        }
    }
}
