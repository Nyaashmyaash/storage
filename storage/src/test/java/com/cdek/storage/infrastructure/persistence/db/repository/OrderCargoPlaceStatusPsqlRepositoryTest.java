package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.model.order.CargoPlaceStatus;
import com.cdek.storage.utils.OrderCargoPlaceTestUtils;
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

import java.time.Instant;
import java.util.List;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { OrderCargoPlaceStatusPsqlRepository.class, OrderPsqlRepository.class,
                        PackagePsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class OrderCargoPlaceStatusPsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();
    @Autowired
    PackagePsqlRepository packageRepository;
    @Autowired
    OrderPsqlRepository orderRepository;
    @Autowired
    OrderCargoPlaceStatusPsqlRepository statusRepository;

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @BeforeEach
    void saveDataInDb() {
        orderRepository.saveNewOrder(OrderTestUtils.createOrderForDb());
        OrderTestUtils.getPackageList().forEach(packageRepository::saveOrUpdatePackage);
        statusRepository.saveNewStatus(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
    }

    @Test
    void update() {
        statusRepository.updateStatus(OrderCargoPlaceTestUtils.createUpdatedCargoPlaceStatusModel1());

        CargoPlaceStatus actual = statusRepository.findStatusByUuid(OrderCargoPlaceTestUtils.STATUS_UUID_1.toString());

        Assertions.assertThat(actual).usingRecursiveComparison()
                .isEqualTo(OrderCargoPlaceTestUtils.createUpdatedCargoPlaceStatusModel1());
    }

    @Test
    void findStatusByUuid_ValidUuid_Success() {
        CargoPlaceStatus actual = statusRepository.findStatusByUuid(OrderCargoPlaceTestUtils.STATUS_UUID_1.toString());

        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel1());
    }

    @Test
    void findCurrentStatusCodeByPackageUuid_ValidUuid_Success() {
        statusRepository.saveNewStatus(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel2());
        String actual = statusRepository.findCurrentStatusCodeByPackageUuid(OrderTestUtils.PACKAGE_1_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceLogisticStatus.CREATED.toString());
    }

    @Test
    void findCurrentStatusByPackageUuid_ValidUuid_Success() {
        statusRepository.saveNewStatus(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel2());
        CargoPlaceStatus actual =
                statusRepository.findCurrentStatusByPackageUuid(OrderTestUtils.PACKAGE_1_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel2());
    }

    @Test
    void getCurrentOfficeUuidByOrderUuid_ValidUuid_Success() {
        statusRepository.saveNewStatus(OrderCargoPlaceTestUtils.createCargoPlaceStatusModel2());
        String actual = statusRepository.getCurrentOfficeUuidByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderCargoPlaceTestUtils.LOCATION_OFFICE_UUID_2.toString());
    }

    @Test
    void getTimestamp_StatusUuid_Success() {
        Instant actual = statusRepository.getTimestamp(OrderCargoPlaceTestUtils.STATUS_UUID_1.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createOrderForDb().getTimestamp());
    }

    @Test
    void getAllStatusByPackageUuid_PackageUuid_Success() {
        statusRepository.saveNewStatus(OrderCargoPlaceTestUtils.createUpdatedCargoPlaceStatusModel2());
        List<String> actual = statusRepository
                .getAllStatusListByPackageUuidAndStatus(OrderTestUtils.PACKAGE_1_UUID.toString(),
                        OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED.name());

        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual.size()).isEqualTo(2);
    }
}
