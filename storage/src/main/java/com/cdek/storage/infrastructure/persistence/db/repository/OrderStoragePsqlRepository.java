package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.OrderStorageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderStoragePsqlRepository implements OrderStorageRepository {

    private final OrderStorageMapper orderStorageMapper;

    @Override
    public void saveNewOrderStorage(@Nonnull OrderStorage orderStorage) {
        orderStorageMapper.insert(orderStorage);
    }

    @Override
    public void updateOrderStorage(@Nonnull OrderStorage orderStorage) {
        orderStorageMapper.update(orderStorage);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String orderStorageUuid) {
        return orderStorageMapper.getTimestamp(orderStorageUuid);
    }

    @Nullable
    @Override
    public OrderStorage findOrderStorageByUuid(@Nonnull String orderStorageUuid) {
        return orderStorageMapper.findOrderStorageByUuid(orderStorageUuid);
    }

    @Override
    public boolean isOrderStorageExists(@Nonnull String orderUuid) {
        return orderStorageMapper.isOrderStorageExists(orderUuid);
    }

    @Nullable
    @Override
    public OrderStorage findOrderStorageByOrderUuid(@Nonnull String orderUuid) {
        return orderStorageMapper.findOrderStorageByOrderUuid(orderUuid);
    }

    @Nonnull
    @Override
    public OrderStorage getOrderStorageByOrderUuid(@Nonnull String orderUuid) {
        return orderStorageMapper.getOrderStorageByOrderUuid(orderUuid);
    }

    @Nullable
    @Override
    public OrderStorage findOrderStorageByOrderNumber(@Nonnull String orderUuid) {
        return orderStorageMapper.findOrderStorageByOrderNumber(orderUuid);
    }

    @Nonnull
    @Override
    public List<OrderStorage> getOrderStorageList(@Nonnull Instant dateFrom, @Nonnull Instant dateTo, int limit) {
        return orderStorageMapper.getOrderStorageList(dateFrom, dateTo, limit);
    }

    @Override
    public void deleteOrderStoragePeriod(@Nonnull String orderNumber) {
        orderStorageMapper.deleteOrderStoragePeriod(orderNumber);
    }

    @Nonnull
    @Override
    public List<String> getOrderNumberWithDuplicateStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo,
            int limit) {
        return orderStorageMapper.getOrderNumberWithDuplicateStoragePeriod(dateFrom, dateTo, limit);
    }
}
