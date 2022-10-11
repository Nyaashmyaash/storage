package com.cdek.storage.infrastructure.converter.order;

import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.InstantConverterImpl;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.utils.OrderCargoPlaceTestUtils;
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
@ContextConfiguration(classes = CargoPlaceStatusConverterTest.CargoPlaceStatusConverterTestConfiguration.class)
class CargoPlaceStatusConverterTest {

    @Autowired
    CargoPlaceStatusConverter converter;

    @Test
    void fromDto_ConvertPackageDtoToModel_Success() {
        var actual = converter.fromDto(OrderCargoPlaceTestUtils.createOrderCargoPlaceDto());
        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
    }

    @Test
    void fromDto_ConvertPackageHistoryDtoToModel_Success() {
        var actual = converter.fromDto(OrderCargoPlaceTestUtils.createCargoPlaceStatusHistoryDto());
        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
    }

    @Configuration
    static class CargoPlaceStatusConverterTestConfiguration {
        @Bean
        public CargoPlaceStatusConverter packageConverter() {
            return new CargoPlaceStatusConverterImpl();
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
