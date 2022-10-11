package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;

@ActiveProfiles("test")
@MybatisTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = { OrderStoragePsqlRepository.class, OrderPsqlRepository.class })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(transactionManager = "transactionManager")
class OrderStoragePsqlRepositoryTest {

    @RegisterExtension
    static final PostgresDBTestContainerExtension extension = new PostgresDBTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        extension.initProperties(registry);
    }

    @Autowired
    OrderPsqlRepository orderRepository;
    @Autowired
    OrderStoragePsqlRepository orderStorageRepository;

    @BeforeEach
    void saveDataInDb() {
        orderRepository.saveNewOrder(OrderTestUtils.createOrderForDb());
        orderStorageRepository.saveNewOrderStorage(OrderStorageTestUtils.createOrderStorage());
    }

    @Test
    void updateOrderStorage_ValidModel_Success() {
        orderStorageRepository.updateOrderStorage(OrderStorageTestUtils.createUpdatedOrderStorage());
        OrderStorage actual = orderStorageRepository.getOrderStorageByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertEquals(OrderStorageTestUtils.createUpdatedOrderStorage(), actual);
    }

    @Test
    void getTimestamp_ValidUuid_Success() {
        Instant actual = orderStorageRepository.getTimestamp(OrderStorageTestUtils.ORDER_STORAGE_UUID_1.toString());

        Assertions.assertEquals(OrderStorageTestUtils.TIMESTAMP, actual);
    }

    @Test
    void isOrderStorageExists_ValidModel_Success() {
        boolean actual = orderStorageRepository.isOrderStorageExists(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertTrue(actual);
    }

    @Test
    void findOrderStorageByUuid_ValidUuid_Success() {
        OrderStorage actual = orderStorageRepository
                .findOrderStorageByUuid(OrderStorageTestUtils.ORDER_STORAGE_UUID_1.toString());

        Assertions.assertEquals(OrderStorageTestUtils.createOrderStorage(), actual);
    }

    @Test
    void getOrderStorageList_DatesAndLimit_ReturnList() {
        Instant dateFrom = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant dateTo = Instant.now().plus(1, ChronoUnit.DAYS);
        List<OrderStorage> actual = orderStorageRepository.getOrderStorageList(dateFrom, dateTo, 2);

        Assertions.assertFalse(actual.isEmpty());
        Assertions.assertEquals(1, actual.size());
    }

    @Test
    void deleteOrderStoragePeriod_OrderNumber_Success() {
        orderStorageRepository.saveNewOrderStorage(OrderStorageTestUtils.createDuplicateOrderStorage());
        orderStorageRepository.deleteOrderStoragePeriod(OrderTestUtils.ORDER_NUMBER);
        var actual = orderStorageRepository.findOrderStorageByOrderUuid(OrderTestUtils.ORDER_UUID.toString());

        Assertions.assertNull(actual);
    }

    @Test
    void getOrderNumberWithDuplicateStoragePeriod_OrderNumberAndTimePeriod_CorrectNumber() {
        Instant dateFrom = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant dateTo = Instant.now().plus(1, ChronoUnit.DAYS);
        orderStorageRepository.saveNewOrderStorage(OrderStorageTestUtils.createDuplicateOrderStorage());
        var actual = orderStorageRepository.getOrderNumberWithDuplicateStoragePeriod(dateFrom, dateTo, 10);

        Assertions.assertFalse(actual.isEmpty());
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(OrderTestUtils.ORDER_NUMBER, actual.get(0));
    }
}
