package com.cdek.storage.application.service;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcDeadlineHelper;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Расчет крайней даты хранения:
 * к дате получения в офисе доставки (или закладки в постамат) прибавить срок хранения по заказу.
 * <a href="https://confluence.cdek.ru/pages/viewpage.action?pageId=78909615">Аналитика</a>
 *
 * После расчета на шину отправляется событие создания сроков хранения.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalcLastStorageDateService implements CalcLastStorageDate {

    private final OrderStorageRepository orderStorageRepository;
    private final OrderStorageEventPublisher eventPublisher;
    private final CalcDeadlineHelper calcDeadlineHelper;

    @Override
    public void calcLastStorageDate(@Nonnull OrderStorage orderStorage) {
        Instant lastDate = calcDeadlineHelper.calcDeadlineOfStoragePeriod(orderStorage);

        orderStorage.setDeadlineForStorage(lastDate);
        orderStorage.setTimestamp(Instant.now());

        orderStorageRepository.updateOrderStorage(orderStorage);
        eventPublisher.publish(orderStorage);
    }
}
