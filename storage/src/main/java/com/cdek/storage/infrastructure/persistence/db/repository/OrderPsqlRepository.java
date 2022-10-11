package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.buffer.ports.output.OrderBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.OrderMapper;
import com.cdek.storage.model.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderPsqlRepository implements OrderBufferRepository, OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public void saveNewOrder(@Nonnull Order order) {
        orderMapper.insert(order);
    }

    @Override
    public void updateOrder(@Nonnull Order order) {
        orderMapper.update(order);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String orderUuid) {
        return orderMapper.getTimestamp(orderUuid);
    }

    @Nullable
    @Override
    public Order findOrderByUuid(@Nonnull String orderUuid) {
        return orderMapper.findOrderByUuid(orderUuid);
    }

    @Nonnull
    @Override
    public String getTrueDeliveryModeCodeByOrderUuid(@Nonnull String orderUuid) {
        return orderMapper.getTrueDeliveryModeCodeByOrderUuid(orderUuid);
    }

    @Override
    public void deleteOrderAndPackages(@Nonnull String orderUuid) {
        orderMapper.deleteOrderAndPackages(orderUuid);
    }

    @Override
    public boolean isOrderExists(@Nonnull String orderUuid) {
        return orderMapper.isOrderExists(orderUuid);
    }

    @Nonnull
    @Override
    public Order getOrderByUuid(@Nonnull String orderUuid) {
        return orderMapper.findOrderByUuid(orderUuid);
    }

    @Nullable
    @Override
    public String findCountDay(@Nonnull String orderUuid) {
        return orderMapper.findCountDay(orderUuid);
    }

    @Nonnull
    @Override
    public String getOrderUuidByOrderNumber(@Nonnull String orderNumber) {
        return orderMapper.getOrderUuidByOrderNumber(orderNumber);
    }

    @Nonnull
    @Override
    public List<String> getOrderNumberListWithoutStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo, int limit) {
        return orderMapper.getOrderNumberListWithoutStoragePeriod(dateFrom, dateTo, limit);
    }
}
