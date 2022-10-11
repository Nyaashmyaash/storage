package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.omnic.integration.client.dto.PostomatEventDto;
import com.cdek.storage.buffer.ports.input.PostamatStatusRefresher;
import com.cdek.stream.lib.MessageProcessorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class PostamatStatusListener extends ExchangeObjectMessageProcessor<PostomatEventDto, Instant> {

    private final PostamatStatusRefresher refresher;

    public PostamatStatusListener(MessageProcessorProperties properties, PostamatStatusRefresher refresher) {
        super(properties, Function.identity());
        this.refresher = refresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<PostomatEventDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .filter(dto -> dto.getOrderUuid() != null)
                .filter(dto -> dto.getOperation() != null)
                .filter(dto -> dto.getDeliveryMode() != null)
                .filter(dto -> dto.getOperationTime() != null)
                .isPresent();
    }

    @Override
    protected boolean processValidation(@Nonnull Message<PostomatEventDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info("Invalid PostomatEventDto, cause UUID: {}.", dto.getUuid());
            return false;
        }
        return true;
    }

    @Override
    protected void processMessage(@Nonnull Message<PostomatEventDto> message, Instant current) {
        refresher.checkStatus(message.getPayload());
    }
}
