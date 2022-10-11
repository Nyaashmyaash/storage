package com.cdek.storage.buffer.refresher;

import com.cdek.omnic.integration.client.dto.PostomatOperationEventDto;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.buffer.ports.input.PostamatStatusRefresher;
import com.cdek.storage.buffer.refresher.helper.CalcAndGetOrderStorageHelper;
import com.cdek.storage.buffer.refresher.helper.CheckOrderInDbAndCreateIfNeedHelper;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.PostamatStatusTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class PostamatStatusStreamRefresherTest {

    @MockBean
    CheckOrderInDbAndCreateIfNeedHelper checkOrderInDbAndCreateIfNeedHelper;
    @MockBean
    CalcLastStorageDate calcLastStorageDate;
    @MockBean
    CalcAndGetOrderStorageHelper calcAndGetOrderStorage;

    PostamatStatusRefresher refresher;

    @BeforeEach
    void before() {
        refresher = new PostamatStatusStreamRefresher(checkOrderInDbAndCreateIfNeedHelper, calcLastStorageDate,
                calcAndGetOrderStorage);
        Mockito.clearInvocations(checkOrderInDbAndCreateIfNeedHelper, calcLastStorageDate, calcAndGetOrderStorage);
    }

    @Test
    void checkStatus_SenderPostamat_SkipEvent() {
        refresher.checkStatus(
                PostamatStatusTestUtils.createEvent(PostomatOperationEventDto.POSTED, "SENDER_POSTAMAT"));

        Mockito.verify(checkOrderInDbAndCreateIfNeedHelper, Mockito.never()).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void checkStatus_StatusReceived_SkipEvent() {
        refresher.checkStatus(
                PostamatStatusTestUtils.createEvent(PostomatOperationEventDto.RECEIVED, "RECEIVER_POSTAMAT"));

        Mockito.verify(checkOrderInDbAndCreateIfNeedHelper, Mockito.never()).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }

    @Test
    void checkStatus_OrderExist_CalcLastStorageDate() {
        Mockito.doNothing().when(checkOrderInDbAndCreateIfNeedHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(calcAndGetOrderStorage.getOrderStoragePeriod(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageWithoutDateOfReceipt());
        Mockito.doNothing().when(calcLastStorageDate).calcLastStorageDate(Mockito.any());

        refresher.checkStatus(
                PostamatStatusTestUtils.createEvent(PostomatOperationEventDto.POSTED, "RECEIVER_POSTAMAT"));

        Mockito.verify(checkOrderInDbAndCreateIfNeedHelper, Mockito.times(1)).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.times(1)).calcLastStorageDate(Mockito.any());
    }

    @Test
    void checkStatus_OrderNotExist_NotCalcLastStorageDate() {
        Mockito.doNothing().when(checkOrderInDbAndCreateIfNeedHelper).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.when(calcAndGetOrderStorage.getOrderStoragePeriod(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.doNothing().when(calcLastStorageDate).calcLastStorageDate(Mockito.any());

        refresher.checkStatus(
                PostamatStatusTestUtils.createEvent(PostomatOperationEventDto.POSTED, "RECEIVER_POSTAMAT"));

        Mockito.verify(checkOrderInDbAndCreateIfNeedHelper, Mockito.times(1)).checkOrderInDbAndCreateIfNeed(Mockito.any());
        Mockito.verify(calcLastStorageDate, Mockito.never()).calcLastStorageDate(Mockito.any());
    }
}
