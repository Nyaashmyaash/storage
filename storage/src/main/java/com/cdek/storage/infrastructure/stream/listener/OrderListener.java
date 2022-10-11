package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.order.esb.client.PackageEventDto;
import com.cdek.storage.buffer.ports.input.OrderRefresher;
import com.cdek.storage.buffer.ports.output.OrderBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderStreamDto;
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
public class OrderListener extends ExchangeObjectMessageProcessor<OrderStreamDto, Instant> {

    private final OrderBufferRepository orderRepository;
    private final OrderRefresher orderRefresher;

    public OrderListener(MessageProcessorProperties properties, OrderBufferRepository orderRepository,
            OrderRefresher refresher) {
        super(properties, Function.identity());
        this.orderRepository = orderRepository;
        this.orderRefresher = refresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<OrderStreamDto> message) {
        return isOrderEventDtoValid(message) && isPackageListValid(message);
    }

    private boolean isOrderEventDtoValid(@Nonnull Message<OrderStreamDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .filter(dto -> dto.getNumber() != null)
                .filter(dto -> dto.getUpdateTimestamp() != null)
                .isPresent();
    }

    private boolean isPackageListValid(@Nonnull Message<OrderStreamDto> message) {
        return message.getPayload().getPackages().stream()
                .allMatch(this::isPackageEventValid);
    }

    private boolean isPackageEventValid(@Nonnull PackageEventDto packageEventDto) {
        return Optional.of(packageEventDto)
                .filter(dto -> dto.getId() != null)
                .isPresent();
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<OrderStreamDto> message) {
        return orderRepository.getTimestamp(message.getPayload().getUuid().toString());
    }

    @Override
    protected void processMessage(@Nonnull Message<OrderStreamDto> message, Instant current) {
        orderRefresher.refreshIfNeeded(message.getPayload(), current);
    }

    @Override
    protected boolean processValidation(@Nonnull Message<OrderStreamDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info(
                    "Invalid OrderEventDto, cause orderUuid: {}, orderNumber: {}, updateTimestamp: {}, " +
                            "PackageEventDto: {}.",
                    dto.getUuid(), dto.getNumber(), dto.getUpdateTimestamp(), dto.getPackages());
            return false;
        }
        return true;
    }
}
