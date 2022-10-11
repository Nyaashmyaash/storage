package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CalcAndGetOrderStorageHelperTest {

    @MockBean
    OrderStoragePsqlRepository storagePsqlRepository;
    @MockBean
    CalcStoragePeriodInDays calcStoragePeriodInDays;
    @MockBean
    OrderPsqlRepository orderPsqlRepository;

    CalcAndGetOrderStorageHelper helper;

    @BeforeEach
    void before() {
        helper = new CalcAndGetOrderStorageHelper(storagePsqlRepository, calcStoragePeriodInDays, orderPsqlRepository);
        Mockito.clearInvocations(storagePsqlRepository, calcStoragePeriodInDays, orderPsqlRepository);
    }

    @Test
    void getOrderStoragePeriod_OrderStorageExist_DontCreate() {
        Mockito.when(storagePsqlRepository.findOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());

        helper.getOrderStoragePeriod(OrderTestUtils.ORDER_UUID.toString());

        Mockito.verify(orderPsqlRepository, Mockito.never()).getOrderByUuid(Mockito.any());
        Mockito.verify(calcStoragePeriodInDays, Mockito.never()).calcStoragePeriod(Mockito.any());
        Mockito.verify(storagePsqlRepository, Mockito.never()).getOrderStorageByOrderUuid(Mockito.any());
    }
}
