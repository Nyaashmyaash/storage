package com.cdek.storage.application.service;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import com.cdek.storage.model.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.UUID;

/**
 * Расчет срока хранения заказа в днях.
 * <a href="https://confluence.cdek.ru/pages/viewpage.action?pageId=78909664">Аналитика</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalcStoragePeriodInDaysService implements CalcStoragePeriodInDays {

    private final OrderStorageRepository orderStorageRepository;
    private final CalcHelper calcHelper;
    private final OrderStorageEventPublisher eventPublisher;
    private final CalcLastStorageDate calcLastStorageDate;

    @Override
    public void calcStoragePeriod(@Nonnull Order order) {

        /**
         * Если у заказа истинный режим доставки "терминал-терминал", то подсчет сроков хранения не происходит.
         *
         * Иначе:
         * 1. Просчитываем сроки хранения.
         * 2. Проверяем есть ли уже сроки хранения в базе.
         * 2.1. Если есть, то проверяем: новый срок хранения = старому сроку хранения из БД.
         * 2.2. Если разное, то присваиваем новое кол-во дней и проверяется дата получения заказа:
         * 2.2.1. Если у сроков хранения уже была известа дата получения в офисе доставки,
         * то пересчитывается крайняя дата хранения заказа и после этого отправляется событие с обновленными данными
         * на шину из сервиса CalcLastStorageDate.
         * 2.2.1. Иначе публикуется событие на шину, в котором известно только обновленное кол-во дней хранения.
         * 2.3. Если одинаковое, то завершаем функцию.
         * 3. Если сроков хранения еще нет в БД, то сохраняем их.
         * 4. Отправляем сроки хранения в днях на шину.
         */
        if (TariffMode.TARIFF_MODE_TT.equals(order.getTrueDeliveryModeCode())) {
            return;
        }

        int countDay = calcHelper.calcCountDay(order);

        if (orderStorageRepository.isOrderStorageExists(order.getOrderUuid())) {
            var orderStorage = orderStorageRepository.getOrderStorageByOrderUuid(order.getOrderUuid());
            if (orderStorage.getShelfLifeOrderInDays().equals(countDay)) {
                log.info("Order with uuid:{} already has storage period.", order.getOrderUuid());
            } else {
                orderStorage.setShelfLifeOrderInDays(countDay);
                orderStorage.setTimestamp(Instant.now());

                orderStorageRepository.updateOrderStorage(orderStorage);

                if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() != null) {
                    calcLastStorageDate.calcLastStorageDate(orderStorage);
                } else {
                    eventPublisher.publish(orderStorage);
                }
            }
        } else {
            var newOrderStorage = OrderStorage.builder()
                    .orderStorageUuid(UUID.randomUUID().toString())
                    .orderUuid(order.getOrderUuid())
                    .orderNumber(order.getOrderNumber())
                    .deadlineForStorage(null)
                    .dateOfReceiptInDeliveryOfficeOrPostamat(null)
                    .shelfLifeOrderInDays(countDay)
                    .timestamp(Instant.now())
                    .build();

            orderStorageRepository.saveNewOrderStorage(newOrderStorage);
            log.info("Successfully save order storage period, with uuid:{} and order uuid:{}.",
                    newOrderStorage.getOrderStorageUuid(), order.getOrderUuid());
            eventPublisher.publish(newOrderStorage);
        }
    }
}
