package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.storage.buffer.ports.input.OfficeRefresher;
import com.cdek.storage.buffer.ports.output.OfficeBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OfficeStreamDto;
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
public class OfficeListener extends ExchangeObjectMessageProcessor<OfficeStreamDto, Instant> {

    private final OfficeBufferRepository officeRepository;
    private final OfficeRefresher refresher;

    public OfficeListener(MessageProcessorProperties properties, OfficeBufferRepository officeRepository,
            OfficeRefresher refresher) {
        super(properties, Function.identity());
        this.officeRepository = officeRepository;
        this.refresher = refresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<OfficeStreamDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .isPresent();
    }

    @Override
    protected boolean processValidation(@Nonnull Message<OfficeStreamDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info("Invalid OfficeEsbEventDto, cause UUID: {}.", dto.getUuid());
            return false;
        }
        return true;
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<OfficeStreamDto> message) {
        return officeRepository.getTimestamp(message.getPayload().getUuid().toString());
    }

    @Override
    protected void processMessage(@Nonnull Message<OfficeStreamDto> message, Instant current) {
        refresher.refreshIfNeeded(message.getPayload(), current);
    }
}
