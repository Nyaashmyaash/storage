package com.cdek.storage.infrastructure.converter.order;

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
@ContextConfiguration(classes = OrderDtoConverterTest.OrderDtoConverterTestConfiguration.class)
class OrderDtoConverterTest {

    @Autowired
    PackageFromPlaceDtoConverter packageFromPlaceDtoConverter;
    @Autowired
    OrderDtoConverter converter;

    @Test
    void fromDto_ConvertOrderDtoWithoutAdditionalServicesToModel_Success() {
        Order actual = converter.fromOrderDto(OrderTestUtils.createOrderDto());
        Order expected = OrderTestUtils.createOrderModelWithoutServicesForConverter();
        expected.setTimestamp(actual.getTimestamp());
        expected.getPackages().get(0).setTimestamp(actual.getTimestamp());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void fromDto_ConvertOrderDtoWithAdditionalServicesToModel_Success() {
        Order actual = converter.fromOrderDto(OrderTestUtils.createOrderWithAdditionalServicesDto());
        Order expected = OrderTestUtils.createOrderModelForConverter();
        expected.setTimestamp(actual.getTimestamp());
        expected.getPackages().get(0).setTimestamp(actual.getTimestamp());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void fromDto_ConvertOrderDtoWithPayerTypeSenderToModel_Success() {
        Order actual = converter.fromOrderDto(OrderTestUtils.createOrderDtoWithPayerTypeSender());
        Order expected = OrderTestUtils.createOrderModelWithoutServicesForConverter();
        expected.setTimestamp(actual.getTimestamp());
        expected.getPackages().get(0).setTimestamp(actual.getTimestamp());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void fromDto_ConvertOrderDtoWithPayerTypeReceiverToModel_Success() {
        Order actual = converter.fromOrderDto(OrderTestUtils.createOrderDtoWithPayerTypeReceiver());
        Order expected = OrderTestUtils.createOrderModelWithoutServicesForConverter();
        expected.setTimestamp(actual.getTimestamp());
        expected.getPackages().get(0).setTimestamp(actual.getTimestamp());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Configuration
    static class OrderDtoConverterTestConfiguration {
        @Bean
        public OrderDtoConverter packageConverter() {
            return new OrderDtoConverterImpl();
        }

        @Bean
        public PackageFromPlaceDtoConverter packageFromPlaceDtoConverter() {
            return new PackageFromPlaceDtoConverterImpl();
        }
    }
}
