package com.cdek.storage.buffer.refresher;

import com.cdek.catalog.common.entity.OrderType;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.buffer.ports.input.OrderRefresher;
import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.buffer.refresher.helper.CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper;
import com.cdek.storage.buffer.refresher.helper.PackageComparerHelper;
import com.cdek.storage.infrastructure.converter.order.OrderConverter;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderStreamDto;
import com.cdek.storage.infrastructure.stream.listener.OrderListener;
import com.cdek.storage.model.order.Order;
import com.cdek.storage.model.order.Package;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStreamRefresher implements OrderRefresher {

    private final OrderConverter converter;
    private final OrderPsqlRepository orderRepository;
    private final PackageBufferRepository packageRepository;
    private final PackageComparerHelper packageComparerHelper;
    private final CompareOldAndNewOrderAndCalcStoragePeriodIfNeedHelper compareHelper;
    private final CalcStoragePeriodInDays calcStoragePeriodInDays;

    /**
     * Метод проверяет нужно ли обновить существующий заказа, либо сохранить новый.
     * Логика работы:
     * 1. Если current == null, то заказ новый и требует сохранения в локальной бд:
     * 1.1. если заказ пришел в статусе "Удален", то данный заказ не сохраняется;
     * 1.2. если заказ ИМ пришел без номерка договора и идентификатора договора, то данный заказ не сохраняется;
     * 2. Если current != null, то это не новый заказ (мы нашли в бд предыдущий таймстамп),
     * поэтому заказ нужно обновить:
     * 2.1. если заказ пришел в статусе "Удален", то удалаяем заказ и пакеты, связанные с ним;
     * 2.2. иначе проводим сверку пакетов
     * (решаем какие пакеты нужно сохранить, обновить, пометить как удаленные) и обновляем заказ;
     * <p>
     * Валидность сообщения проверяется в {@link OrderListener}
     *
     * @param newDto  новое сообщение с шины\кролика.
     * @param current таймстамп из бд.
     */
    @Transactional
    @Override
    public void refreshIfNeeded(@Nonnull OrderStreamDto newDto, @Nullable Instant current) {
        var newOrder = converter.fromDto(newDto);
        List<Package> packs = newOrder.getPackages();
        if (current != null) {
            this.updateOrderAndPackages(newOrder, packs);
            log.info("Order, with uuid:{} successfully updated.", newOrder.getOrderUuid());
        } else {
            if (Boolean.TRUE.equals(newOrder.getDeleted())) {
                log.info("Skip order, with uuid:{}, cause is deleted.", newOrder.getOrderUuid());
            } else if (OrderType.ORDER_TYPE_ONLINE_SHOP.equals(newOrder.getOrderTypeCode())
                    && newOrder.getPayerContractNumber() == null
                    && newOrder.getPayerContractUuid() == null) {
                log.info("Skip order, with uuid:{}, cause contract number and contract uuid not exist.",
                        newOrder.getOrderUuid());
            } else {
                this.saveOrderAndPackages(newOrder, packs);
                log.info("Successfully save order, with uuid:{}.", newOrder.getOrderUuid());
            }
        }
    }

    private void saveOrderAndPackages(@Nonnull Order order, @Nonnull List<Package> packs) {
        orderRepository.saveNewOrder(order);
        packs.forEach(packageRepository::saveOrUpdatePackage);
        calcStoragePeriodInDays.calcStoragePeriod(order);
    }

    private void updateOrderAndPackages(@Nonnull Order newOrder, @Nonnull List<Package> packs) {
        if (Boolean.TRUE.equals(newOrder.getDeleted())) {
            orderRepository.deleteOrderAndPackages(newOrder.getOrderUuid());
            return;
        }

        var oldOrder = orderRepository.getOrderByUuid(newOrder.getOrderUuid());
        orderRepository.updateOrder(newOrder);
        packageComparerHelper
                .comparePackages(packageRepository.getAllNotDeletedPackageUuids(newOrder.getOrderUuid()), packs);

        //проверяется изменились ли данные заказа, и если да, то пересчитывается срок хранения
        compareHelper.compareOrdersFieldsAndCalcStoragePeriod(newOrder, oldOrder);
    }
}
