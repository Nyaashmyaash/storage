package com.cdek.storage.infrastructure.converter.contract;

import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.infrastructure.converter.common.UuidConverterImpl;
import com.cdek.storage.model.contract.Contract;
import com.cdek.storage.utils.ContractTestUtils;
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
@ContextConfiguration(classes = ContractConverterTest.ContractConverterTestConfiguration.class)
class ContractConverterTest {

    @Autowired
    ContractConverter converter;

    @Test
    void fromDto_ConvertContractDtoToModel_Success() {
        Contract actual = converter.fromDto(ContractTestUtils.createContractEventDtoTypeIM());
        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.createContractIMModel());
    }

    @Configuration
    static class ContractConverterTestConfiguration {
        @Bean
        public ContractConverter contractConverter() {
            return new ContractConverterImpl();
        }

        @Bean
        public UuidConverter uuidConverter() {
            return new UuidConverterImpl();
        }

        @Bean
        public SellerConverter sellerConverter() {
            return new SellerConverterImpl();
        }
    }
}
