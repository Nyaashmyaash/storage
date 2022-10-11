package com.cdek.storage.infrastructure.converter.storage;

import com.cdek.storage.client.esb.OrderStorageEventDto;
import com.cdek.storage.infrastructure.converter.common.InstantConverter;
import com.cdek.storage.infrastructure.converter.common.InstantConverterImpl;
import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.utils.OrderStorageTestUtils;
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
@ContextConfiguration(classes = OrderStorageEventDtoConverterTest.OrderStorageEventDtoConverterTestConfiguration.class)
class OrderStorageEventDtoConverterTest {

    @Autowired
    OrderStorageEventDtoConverter converter;

    @Test
    void fromDto_ConvertPackageDtoToModel_Success() {
        OrderStorageEventDto actual = converter.fromOrderStorage(OrderStorageTestUtils.createOrderStorage());
        Assertions.assertThat(actual).isEqualToComparingFieldByField(OrderStorageTestUtils.createOrderStorageEventDto());
    }

    @Configuration
    static class OrderStorageEventDtoConverterTestConfiguration {

        @Bean
        public InstantConverter instantConverter() {
            return new InstantConverterImpl();
        }

        @Bean
        public UuidConverter uuidConverter() {
            return new UuidConverterImpl();
        }

        @Bean
        public OrderStorageEventDtoConverter converter() {
            return new OrderStorageEventDtoConverterImpl();
        }
    }
}
