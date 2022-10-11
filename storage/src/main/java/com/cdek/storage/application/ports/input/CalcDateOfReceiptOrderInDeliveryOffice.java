package com.cdek.storage.application.ports.input;

import com.cdek.storage.application.model.OrderStorage;

import javax.annotation.Nonnull;

public interface CalcDateOfReceiptOrderInDeliveryOffice {

    void calcDateOfReceiptOrder(
            @Nonnull String orderUuid,
            @Nonnull String deliveryMode,
            @Nonnull OrderStorage orderStorage
    );
}
