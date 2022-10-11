package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.contract.Seller;
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

import java.util.List;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { SellerPsqlRepository.class, ContractPsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class SellerPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    ContractPsqlRepository contractRepository;
    @Autowired
    SellerPsqlRepository sellerRepository;

    @BeforeEach
    void saveNewSellerInDb() {
        contractRepository.saveNewContract(ContractTestUtils.createContractForDb());
        ContractTestUtils.getSellersList().forEach(sellerRepository::saveOrUpdateSeller);
    }

    @Test
    void saveSeller_ValidModel_Success() {
        Seller actual = sellerRepository.findSellerById(ContractTestUtils.SELLER_ID_1);

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.createSellerModel1());
    }

    @Test
    void getAllSellerId_ContractUuid_Success() {
        List<Long> actual = sellerRepository.getAllSellerId(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.getSellerIdList());
    }

    @Test
    void deleteSellerList_SellerIdList_Success() {
        sellerRepository.deleteSellerList(ContractTestUtils.getSellerIdList());
        List<Long> actual = sellerRepository.getAllSellerId(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void findSellerListByContractUuid_ContractUuid_Success() {
        List<Seller> actual = sellerRepository.findSellerListByContractUuid(ContractTestUtils.CONTRACT_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(ContractTestUtils.getSellersList());
    }
}
