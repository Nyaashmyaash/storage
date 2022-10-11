package com.cdek.storage.application.service;

import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcDeadlineHelper;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import com.cdek.storage.utils.OrderStorageTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CalcLastStorageDateServiceTest {

    @MockBean
    OrderStorageRepository orderStorageRepository;
    @MockBean
    OrderStorageEventPublisher orderStorageEventPublisher;
    @MockBean
    CalcDeadlineHelper calcDeadlineHelper;

    CalcLastStorageDate calcLastStorageDateService;

    @BeforeEach
    void before() {
        calcLastStorageDateService = new CalcLastStorageDateService(orderStorageRepository, orderStorageEventPublisher,
                calcDeadlineHelper);
        Mockito.clearInvocations(orderStorageRepository, orderStorageEventPublisher, calcDeadlineHelper);
    }

    @Test
    void calcLastStorageDate_OrderStorage_Success() {
        Mockito.when(calcDeadlineHelper.calcDeadlineOfStoragePeriod(Mockito.any())).thenReturn(Instant.now());

        calcLastStorageDateService.calcLastStorageDate(OrderStorageTestUtils.createOrderStorage());
        Mockito.verify(orderStorageRepository, Mockito.times(1)).updateOrderStorage(Mockito.any());
        Mockito.verify(orderStorageEventPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(calcDeadlineHelper, Mockito.times(1)).calcDeadlineOfStoragePeriod(Mockito.any());
    }
}
