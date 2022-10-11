package com.cdek.storage.infrastructure.converter.order;

import com.cdek.storage.model.order.Package;
import com.cdek.storage.utils.OrderTestUtils;
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
@ContextConfiguration(classes = PackageConverterTest.PackageConverterTestConfiguration.class)
class PackageConverterTest {

    @Autowired
    PackageConverter converter;

    @Test
    void fromDto_ConvertPackageDtoToModel_Success() {
        Package actual = converter.fromEventDto(OrderTestUtils.createPackage1EventDto());
        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createPackage1ModelFromEsb());
    }

    @Configuration
    static class PackageConverterTestConfiguration {
        @Bean
        public PackageConverter packageConverter() {
            return new PackageConverterImpl();
        }
    }
}
