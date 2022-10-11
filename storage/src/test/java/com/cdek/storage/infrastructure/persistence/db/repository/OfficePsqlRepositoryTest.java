package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.office.Office;
import com.cdek.storage.utils.OfficeTestUtils;
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
                classes = OfficePsqlRepository.class)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class OfficePsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    OfficePsqlRepository officeRepository;

    @BeforeEach
    void saveNewOfficeInDb() {
        officeRepository.saveNewOffice(OfficeTestUtils.createOfficeModel());
    }

    @Test
    void updateOffice_ValidModel_Success() {
        officeRepository.updateOffice(OfficeTestUtils.createUpdatedOfficeModel());
        Office actual = officeRepository.getOfficeByUuid(OfficeTestUtils.OFFICE_UUID.toString());
        Assertions.assertThat(actual).isEqualTo(OfficeTestUtils.createUpdatedOfficeModel());
    }

    @Test
    void getTimestamp_ValidModel_Success() {
        Instant actual = officeRepository.getTimestamp(OfficeTestUtils.OFFICE_UUID.toString());
        Assertions.assertThat(actual).isEqualTo(OfficeTestUtils.TIMESTAMP);
    }

    @Test
    void findOffice_ValidModel_Success() {
        Office actual = officeRepository.getOfficeByUuid(OfficeTestUtils.OFFICE_UUID.toString());
        Assertions.assertThat(actual).isEqualTo(OfficeTestUtils.createOfficeModel());
    }
}
