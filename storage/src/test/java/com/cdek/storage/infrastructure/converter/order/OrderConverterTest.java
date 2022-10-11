package com.cdek.storage.infrastructure.converter.order;

import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.InstantConverterImpl;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.model.order.Order;
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
@ContextConfiguration(classes = OrderConverterTest.OrderConverterTestConfiguration.class)
class OrderConverterTest {

    @Autowired
    OrderConverter converter;

    @Test
    void fromDto_ConvertOrderDtoToModel_Success() {
        Order actual = converter.fromDto(OrderTestUtils.createOrderEventDto());
        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createOrderModelWithoutContractNumber());
    }

    @Test
    void fromDto_ConvertOrderDtoToModelWithoutServices_Success() {
        Order actual = converter.fromDto(OrderTestUtils.createOrder());
        Assertions.assertThat(actual)
                .isEqualTo(OrderTestUtils.createOrderModelWithoutServicesAndWithoutContractNumber());
    }

    @Configuration
    static class OrderConverterTestConfiguration {
        @Bean
        public OrderConverter orderConverter() {
            return new OrderConverterImpl();
        }

        @Bean
        public PackageConverter packageConverter() {
            return new PackageConverterImpl();
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
