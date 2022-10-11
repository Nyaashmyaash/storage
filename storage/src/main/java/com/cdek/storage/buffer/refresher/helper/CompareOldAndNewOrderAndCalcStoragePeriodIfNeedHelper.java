package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.application.service.helper.TariffModeLists;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import com.cdek.storage.model.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Сервис для проверки дабавилась/изменилась ли доп. услуга "Хранение на складе", или изменился тариф, и если "да",
 * то запускается перерасчет сроков хранения.
 */
@Component
@RequiredArgsConstructor
public class CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper {

    private final OrderStoragePsqlRepository storagePsqlRepository;
    private final CalcLastStorageDate calcLastStorageDate;
    private final CalcHelper calcHelper;
    private final OrderStorageEventPublisher eventPublisher;

    public void compareOrdersFieldsAndCalcStoragePeriod(@Nonnull Order newOrder, @Nonnull Order oldOrder) {
        final var newTariffMode = newOrder.getTrueDeliveryModeCode();
        final var oldTariffMode = oldOrder.getTrueDeliveryModeCode();
        final var oldCountDay = Optional.of(oldOrder)
                .map(Order::getCountDay)
                .orElse(0);
        final var newCountDay = Optional.of(newOrder)
                .map(Order::getCountDay)
                .orElse(0);

        if (TariffModeLists.getTariffModeToPostamatList().contains(oldTariffMode)
                && TariffModeLists.getTariffModeToDoorOrToWarehouseList().contains(newTariffMode)) {
            var orderStorage = storagePsqlRepository.getOrderStorageByOrderUuid(newOrder.getOrderUuid());
            final int countDay = calcHelper.calcCountDay(newOrder);
            orderStorage.setShelfLifeOrderInDays(countDay);

            if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() != null) {
                calcLastStorageDate.calcLastStorageDate(orderStorage);
            } else {
                storagePsqlRepository.updateOrderStorage(orderStorage);
                eventPublisher.publish(orderStorage);
            }

            return;
        }

        if (isNeedCalcStoragePeriodWithNewTariffMode(newTariffMode, oldTariffMode)) {
            final var orderStorage = storagePsqlRepository.getOrderStorageByOrderUuid(newOrder.getOrderUuid());
            if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() == null) {
                int countDay = calcHelper.calcCountDay(newOrder);
                orderStorage.setShelfLifeOrderInDays(countDay);
                storagePsqlRepository.updateOrderStorage(orderStorage);
                eventPublisher.publish(orderStorage);
            }
        }

        if (!newCountDay.equals(oldCountDay)) {
            var orderStorage = storagePsqlRepository.getOrderStorageByOrderUuid(newOrder.getOrderUuid());
            if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() == null) {
                int countDay = calcHelper.calcCountDay(newOrder);
                orderStorage.setShelfLifeOrderInDays(countDay);
                storagePsqlRepository.updateOrderStorage(orderStorage);
                eventPublisher.publish(orderStorage);
            } else {
                calcLastStorageDateWithNewStoragePeriodIsDays(orderStorage, newCountDay, oldCountDay);
            }
        }
    }

    /**
     * Если новый и старый тарифы одинаковые, то перерасчет срока хранения не нужен.
     * Иначе проверяется, относятся ли тарифы к типу "До постамата":
     * 1. если относятся оба, то перерасчет не происходит,
     * 2. если только один из них, то перерасчет необходим.
     */
    private boolean isNeedCalcStoragePeriodWithNewTariffMode(@Nonnull String newTariffMode,
            @Nonnull String oldTariffMode) {
        if (newTariffMode.equals(oldTariffMode)) {
            return false;
        }

        final var isNewTariffModeIsPostamat = TariffModeLists.getTariffModeToPostamatList().contains(newTariffMode);
        final var isOldTariffModeIsPostamat = TariffModeLists.getTariffModeToPostamatList().contains(oldTariffMode);

        return isNewTariffModeIsPostamat && !isOldTariffModeIsPostamat
                || !isNewTariffModeIsPostamat && isOldTariffModeIsPostamat;
    }

    private void calcLastStorageDateWithNewStoragePeriodIsDays(@Nonnull OrderStorage orderStorage, int newCountDay,
            int oldCountDay) {
        final int oldStoragePeriodInDays = orderStorage.getShelfLifeOrderInDays();
        final int differenceBetweenNewAndOldCountPaidDay = newCountDay - oldCountDay;
        final int newStoragePeriodInDays = oldStoragePeriodInDays + differenceBetweenNewAndOldCountPaidDay;

        orderStorage.setShelfLifeOrderInDays(newStoragePeriodInDays);

        storagePsqlRepository.updateOrderStorage(orderStorage);
        calcLastStorageDate.calcLastStorageDate(orderStorage);
    }
}
