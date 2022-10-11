package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.utils.OrderTestUtils;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { OrderPsqlRepository.class, PackagePsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class OrderPsqlRepositoryTest {

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
    void saveNewOrderInDb() {
        orderRepository.saveNewOrder(OrderTestUtils.createOrderForDb());
        OrderTestUtils.getPackageList().forEach(packageRepository::saveOrUpdatePackage);
    }

    @Test
    void saveOrder_ValidModel_Success() {
        final var actual = orderRepository.findOrderByUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createOrderForDb());
    }

    @Test
    void updateOrder_ValidUpdatedModel_Success() {
        orderRepository.updateOrder(OrderTestUtils.createUpdatedOrderForDb());
        final var actual = orderRepository.findOrderByUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createUpdatedOrderForDb());
    }

    @Test
    void getTimestamp_OrderUuid_Success() {
        final var actual = orderRepository.getTimestamp(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createOrderForDb().getTimestamp());
    }

    @Test
    void getTrueDeliveryModeCodeByOrderUuid_OrderUuid_Success() {
        final var actual = orderRepository.getTrueDeliveryModeCodeByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(TariffMode.TARIFF_MODE_DD);
    }

    @Test
    void deleteOrder_OrderUuid_Success() {
        orderRepository.deleteOrderAndPackages(OrderTestUtils.ORDER_UUID.toString());

        final var orderAfterDeletion = orderRepository.findOrderByUuid(OrderTestUtils.ORDER_UUID.toString());
        final var packagesAfterDeletion =
                packageRepository.findAllPackagesByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(orderAfterDeletion).isNull();
        Assertions.assertThat(packagesAfterDeletion).isEmpty();
    }

    @Test
    void isOrderExists_OrderUuid_ReturnTrue() {
        final var actual = orderRepository.isOrderExists(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void isOrderExists_OrderUuid_ReturnFalse() {
        final var actual = orderRepository.isOrderExists(UUID.randomUUID().toString());

        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void getOrderByUuid_OrderUuid_Success() {
        final var actual = orderRepository.getOrderByUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createOrderForDb());
    }

    @Test
    void findCountDay_OrderUuid_Success() {
        final var actual = orderRepository.findCountDay(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo("15");
    }

    @Test
    void saveOrderAndUpdateOnConflict_Order_Success() {
        orderRepository.saveNewOrder(OrderTestUtils.createUpdatedOrderForDb());
        final var actual = orderRepository.getOrderByUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.createUpdatedOrderForDb());
    }

    @Test
    void getOrderUuidByOrderNumber_OrderNumber_Success() {
        final var actual = orderRepository.getOrderUuidByOrderNumber(OrderTestUtils.ORDER_NUMBER);

        Assertions.assertThat(actual).isEqualTo(OrderTestUtils.ORDER_UUID.toString());
    }

    @Test
    void getOrderNumberListWithoutStoragePeriod_OrderNumber_Success() {
        final var actual = orderRepository.getOrderNumberListWithoutStoragePeriod(
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(1, ChronoUnit.DAYS),
                2);

        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual.size()).isEqualTo(1);
        Assertions.assertThat(actual.get(0)).isEqualTo(OrderTestUtils.ORDER_NUMBER);
    }
}
