package com.cdek.storage.buffer.refresher;

import com.cdek.omnic.integration.client.dto.PostomatEventDto;
import com.cdek.omnic.integration.client.dto.PostomatOperationEventDto;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.buffer.ports.input.PostamatStatusRefresher;
import com.cdek.storage.buffer.refresher.helper.CalcAndGetOrderStorageHelper;
import com.cdek.storage.buffer.refresher.helper.CheckOrderInDbAndCreateIfNeedHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.UUID;

/**
 *  Проверяется пришедший статус с шины и расчитывается крайняя дата хранения для заказов в режиме "до постамат".
 *
 * Если статус != POSTED и признак режима доставки != RECEIVER_POSTAMAT, то завершается работа метода.
 * Иначе:
 * 1. Проверяется существует ли заказ в БД.
 * 1.1. Если нет, то происходит получение заказа по запросу, сохранение его в БД и расчет срока хранения для него.
 * 2. Проверяется наличие срока хранения в БД
 * 2.1 Если срок хранения отсутствует, то вызывается метод расчета и сохранения его в БД.
 * 3. В объект "Срок хранения" вставляется дата и время закладки заказа в постамат, если она отсутствовала.
 * 4. Вызывается метод расчета крайней даты хранения заказа в постамате.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostamatStatusStreamRefresher implements PostamatStatusRefresher {

    private static final String RECEIVER_POSTAMAT = "RECEIVER_POSTAMAT";

    private final CheckOrderInDbAndCreateIfNeedHelper checkOrderInDbAndCreateIfNeedHelper;
    private final CalcLastStorageDate calcLastStorageDate;
    private final CalcAndGetOrderStorageHelper calcAndGetOrderStorage;

    @Override
    public void checkStatus(@Nonnull PostomatEventDto dto) {
        if (!PostomatOperationEventDto.POSTED.equals(dto.getOperation())
                || !RECEIVER_POSTAMAT.equals(dto.getDeliveryMode())) {
            return;
        }

        checkOrderInDbAndCreateIfNeedHelper.checkOrderInDbAndCreateIfNeed(dto.getOrderUuid().toString());
        setDateOfReceiptAndCalcLastStorageDate(dto.getOrderUuid(), dto.getOperationTime());
    }

    private void setDateOfReceiptAndCalcLastStorageDate(@Nonnull UUID orderUuid, @Nonnull Instant dateOfReceipt) {
        var orderStorage = calcAndGetOrderStorage.getOrderStoragePeriod(orderUuid.toString());

        if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() == null) {
            orderStorage.setDateOfReceiptInDeliveryOfficeOrPostamat(dateOfReceipt);
            calcLastStorageDate.calcLastStorageDate(orderStorage);
        }
    }
}
