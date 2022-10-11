package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.contract.Contract;
import com.cdek.storage.utils.ContractTestUtils;
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
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ContractPsqlRepository.class)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class ContractPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    ContractPsqlRepository contractRepository;

    @BeforeEach
    void saveNewContractInDb() {
        contractRepository.saveNewContract(ContractTestUtils.createContractForDb());
    }

    @Test
    void saveContract_ValidModel_Success() {
        Contract actual = contractRepository.findContractByUuid(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.createContractForDb());
    }

    @Test
    void updateContract_ValidUpdatedModel_Success() {
        contractRepository.updateContract(ContractTestUtils.createUpdatedContractForDb());
        Contract actual = contractRepository.findContractByUuid(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.createUpdatedContractForDb());
    }

    @Test
    void getTimestamp_ContractUuid_Success() {
        Instant actual = contractRepository.getTimestamp(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.createContractForDb().getTimestamp());
    }

    @Test
    void getContractUuidByContractNumber_ContractNumber_Success() {
        String actual = contractRepository.getContractUuidByContractNumber(ContractTestUtils.CONTRACT_NUMBER);

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.CONTRACT_UUID.toString());
    }
}
