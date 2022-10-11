package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CalcAndGetOrderStorageHelper {

    private final OrderStoragePsqlRepository storagePsqlRepository;
    private final CalcStoragePeriodInDays calcStoragePeriodInDays;
    private final OrderPsqlRepository orderPsqlRepository;

    @Nonnull
    public OrderStorage getOrderStoragePeriod(@Nonnull String orderUuid) {
        return Optional.ofNullable(
                storagePsqlRepository.findOrderStorageByOrderUuid(orderUuid))
                .orElseGet(() -> this.calcAndGetOrderStorage(orderUuid));
    }

    @Nonnull
    private OrderStorage calcAndGetOrderStorage(@Nonnull String orderUuid) {
        final var order = orderPsqlRepository.getOrderByUuid(orderUuid);
        calcStoragePeriodInDays.calcStoragePeriod(order);
        return storagePsqlRepository.getOrderStorageByOrderUuid(order.getOrderUuid());
    }
}
