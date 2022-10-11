package com.cdek.storage.application.ports.input;

import com.cdek.storage.model.order.Order;

import javax.annotation.Nonnull;

public interface CalcStoragePeriodInDays {

    void calcStoragePeriod(@Nonnull Order order);
}
