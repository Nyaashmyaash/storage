package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.storage.buffer.ports.input.OrderCargoPlaceStatusRefresher;
import com.cdek.storage.buffer.ports.output.OrderCargoPlaceStatusBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderCargoPlaceStreamDto;
import com.cdek.stream.lib.MessageProcessorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class OrderCargoPlaceListener extends ExchangeObjectMessageProcessor<OrderCargoPlaceStreamDto, Instant> {

    private final OrderCargoPlaceStatusRefresher cargoPlaceRefresher;
    private final OrderCargoPlaceStatusBufferRepository orderCargoPlaceBufferRepository;

    public OrderCargoPlaceListener(MessageProcessorProperties properties,
            OrderCargoPlaceStatusRefresher cargoPlaceRefresher,
            OrderCargoPlaceStatusBufferRepository orderCargoPlaceBufferRepository) {
        super(properties, Function.identity());
        this.cargoPlaceRefresher = cargoPlaceRefresher;
        this.orderCargoPlaceBufferRepository = orderCargoPlaceBufferRepository;
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<OrderCargoPlaceStreamDto> message) {
        return Optional.of(message)
                .map(Message::getPayload)
                .map(OrderCargoPlaceStreamDto::getUuid)
                .map(UUID::toString)
                .map(orderCargoPlaceBufferRepository::getTimestamp)
                .orElse(null);
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<OrderCargoPlaceStreamDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .filter(dto -> dto.getUpdateTimestamp() != null)
                .isPresent();
    }

    @Override
    protected void processMessage(@Nonnull Message<OrderCargoPlaceStreamDto> message, @Nullable Instant current) {
        cargoPlaceRefresher.saveIfNeeded(message.getPayload(), current);
    }

    @Override
    protected boolean processValidation(@Nonnull Message<OrderCargoPlaceStreamDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info(
                    "Invalid OrderCargoPlaceDto, cause cargoPlaceUuid: {}, updateTimestamp: {}.",
                    dto.getUuid(), dto.getUpdateTimestamp());
            return false;
        }
        return true;
    }
}
