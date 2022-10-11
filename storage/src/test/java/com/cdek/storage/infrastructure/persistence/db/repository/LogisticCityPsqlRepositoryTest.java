package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.logistic.LogisticCity;
import com.cdek.storage.utils.LogisticCityTestUtils;
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
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = LogisticCityPsqlRepository.class)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class LogisticCityPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    LogisticCityPsqlRepository logisticCityRepository;

    @BeforeEach
    void saveNewCityInDb() {
        logisticCityRepository.saveNewLogisticCity(LogisticCityTestUtils.createLogisticCityModel());
    }

    @Test
    void updateLogisticCity_ValidModel_Success() {
        logisticCityRepository.updateLogisticCity(LogisticCityTestUtils.createUpdatedLogisticCityModel());
        LogisticCity actual = logisticCityRepository.getLogisticCity(String.valueOf(LogisticCityTestUtils.CITY_CODE));
        Assertions.assertThat(actual).isEqualTo(LogisticCityTestUtils.createUpdatedLogisticCityModel());
    }

    @Test
    void getTimestamp_ValidModel_Success() {
        Instant actual = logisticCityRepository.getTimestamp(LogisticCityTestUtils.CITY_UUID.toString());
        Assertions.assertThat(actual).isEqualTo(LogisticCityTestUtils.TIMESTAMP);
    }

    @Test
    void findLogisticCity_ValidModel_Success() {
        LogisticCity actual = logisticCityRepository.getLogisticCity(String.valueOf(LogisticCityTestUtils.CITY_CODE));
        Assertions.assertThat(actual).isEqualTo(LogisticCityTestUtils.createLogisticCityModel());
    }
}
