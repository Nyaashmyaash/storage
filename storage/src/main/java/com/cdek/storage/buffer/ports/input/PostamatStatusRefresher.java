package com.cdek.storage.buffer.ports.input;

import com.cdek.omnic.integration.client.dto.PostomatEventDto;

import javax.annotation.Nonnull;

public interface PostamatStatusRefresher {
    void checkStatus(@Nonnull PostomatEventDto dto);
}
