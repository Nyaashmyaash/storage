package com.cdek.storage.buffer.ports.input;

import com.cdek.contract.esb.client.ContractEventDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public interface ContractRefresher {

    void refreshIfNeeded(@Nonnull ContractEventDto newDto, @Nullable Instant current);
}
