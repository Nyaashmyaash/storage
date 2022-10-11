package com.cdek.storage.infrastructure.service.fix.tasks;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcDeadlineHelper;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class FixOrderStorageTask implements Runnable {

    private final CalcDeadlineHelper calcDeadlineHelper;
    private final OrderStorageRepository orderStorageRepository;
    private final OrderStorage orderStorage;
    private final OrderStorageEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final CalcHelper calcHelper;

    @Override
    public void run() {
        try {
            var order = orderRepository.getOrderByUuid(orderStorage.getOrderUuid());
            var oldCountDays = orderStorage.getShelfLifeOrderInDays();
            var newCountDays = calcHelper.calcCountDay(order);

            orderStorage.setShelfLifeOrderInDays(newCountDays);
            orderStorage.setTimestamp(Instant.now());

            //Если новая крайняя дата хранения не равна старой дате
            //или новое кол-во дней не равно старому кол-ву,
            //то публикуем событие с новыми данными.
            if (!isDeadlineEquals(orderStorage) || !oldCountDays.equals(newCountDays)) {
                eventPublisher.publish(orderStorage);
            }

            orderStorageRepository.updateOrderStorage(orderStorage);
        } catch (Exception e) {
            log.error("An error occurred at fixOrderStorage with uuid {}", orderStorage.getOrderStorageUuid(), e);
        }
    }

    private boolean isDeadlineEquals(@Nonnull OrderStorage orderStorage) {
        var result = true;
        if (orderStorage.getDeadlineForStorage() != null) {
            var oldLastDate = orderStorage.getDeadlineForStorage();
            var newLastDate = calcDeadlineHelper.calcDeadlineOfStoragePeriod(orderStorage);
            orderStorage.setDeadlineForStorage(newLastDate);
            result = oldLastDate.equals(newLastDate);
        }
        return result;
    }
}
