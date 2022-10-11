package com.cdek.storage.infrastructure.stream.publisher;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.client.esb.OrderStorageEventDto;
import com.cdek.storage.infrastructure.converter.storage.OrderStorageEventDtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStorageEventPublisher {

    private final StreamBridge streamBridge;
    private final OrderStorageEventDtoConverter converter;

    public void publish(OrderStorage orderStorage) {
        OrderStorageEventDto dto = converter.fromOrderStorage(orderStorage);

        final var message = MessageBuilder.withPayload(dto).build();

        log.info("Start publish event {}", message.getPayload());
        try {
            streamBridge.send("objStoragePeriod-out-0", message);
        } catch (Exception ex) {
            log.error("Event publish error {}", ex);
        }
        log.info("Finish publish event with uuid:{}", message.getPayload().getUuid());
    }
}
