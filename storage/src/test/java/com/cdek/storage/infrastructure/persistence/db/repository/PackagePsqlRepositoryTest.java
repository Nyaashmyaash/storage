package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.order.Package;
import com.cdek.storage.utils.OrderTestUtils;
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

import java.util.Collections;
import java.util.List;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { PackagePsqlRepository.class, OrderPsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class PackagePsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    OrderPsqlRepository orderRepository;
    @Autowired
    PackagePsqlRepository packageRepository;

    @BeforeEach
    void saveNewPackageInDb() {
        orderRepository.saveNewOrder(OrderTestUtils.createOrderForDb());
        OrderTestUtils.getListWithDeletedPackage().forEach(packageRepository::saveOrUpdatePackage);
    }

    @Test
    void savePackage_ValidModel_Success() {
        Package actual = packageRepository.findPackageByUuid(OrderTestUtils.PACKAGE_1_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createPackage1Model());
    }

    @Test
    void getNotDeletedPackagesByOrderUuid_ValidModel_Success() {
        List<Package> packageList = packageRepository
                .getNotDeletedPackagesByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(packageList.size()).isEqualTo(2);
        Assertions.assertThat(packageList).isEqualTo(OrderTestUtils.getPackageList());
    }

    @Test
    void findAllPackagesByOrderUuid_ValidModel_Success() {
        List<Package> packageList = packageRepository.findAllPackagesByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(packageList.size()).isEqualTo(3);
        Assertions.assertThat(packageList).isEqualTo(OrderTestUtils.getListWithDeletedPackage());
    }

    @Test
    void setPackagesIsDeleted_ValidModel_Success() {
        packageRepository.setPackagesIsDeleted(Collections.singletonList(OrderTestUtils.PACKAGE_1_UUID.toString()));
        Package actual = packageRepository.findPackageByUuid(OrderTestUtils.PACKAGE_1_UUID.toString());

        Assertions.assertThat(actual.getDeleted()).isTrue();
    }

    @Test
    void setAllPackagesIsDeleted_ValidModel_Success() {
        packageRepository.setAllPackagesIsDeleted(OrderTestUtils.ORDER_UUID.toString());
        List<Package> actual =
                packageRepository.getNotDeletedPackagesByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void getAllNotDeletedPackageUuids_ValidModel_Success() {
        List<String> actual = packageRepository.getAllNotDeletedPackageUuids(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.getAllNotDeletedPackageUuids());
    }
}
