package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.contract.esb.client.ContractEventDto;
import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.storage.buffer.ports.input.ContractRefresher;
import com.cdek.storage.buffer.ports.output.ContractBufferRepository;
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
public class ContractListener extends ExchangeObjectMessageProcessor<ContractEventDto, Instant> {

    private final ContractBufferRepository contractRepository;
    private final ContractRefresher contractRefresher;

    public ContractListener(MessageProcessorProperties properties, ContractBufferRepository contractRepository,
            ContractRefresher contractRefresher) {
        super(properties, Function.identity());
        this.contractRepository = contractRepository;
        this.contractRefresher = contractRefresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<ContractEventDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .filter(dto -> dto.getNumber() != null)
                .filter(dto -> dto.getUpdateTimestamp() != null)
                .isPresent();
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<ContractEventDto> message) {
        return contractRepository.getTimestamp(message.getPayload().getUuid().toString());
    }

    @Override
    protected void processMessage(@Nonnull Message<ContractEventDto> message, Instant current) {
        contractRefresher.refreshIfNeeded(message.getPayload(), current);
    }

    @Override
    protected boolean processValidation(@Nonnull Message<ContractEventDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info(
                    "Invalid ContractEventDto, cause contractUuid: {}, contractNumber: {}, updateTimestamp: {}.",
                    dto.getUuid(), dto.getNumber(), dto.getUpdateTimestamp());
            return false;
        }
        return true;
    }
}
