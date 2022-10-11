package com.cdek.storage.application.ports.input;

import com.cdek.storage.application.model.OrderStorage;

import javax.annotation.Nonnull;

public interface CalcLastStorageDate {

    void calcLastStorageDate(@Nonnull OrderStorage orderStorage);
}
