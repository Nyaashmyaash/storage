package com.cdek.storage.infrastructure.controller.api.impl;

import com.cdek.storage.infrastructure.controller.api.ApiSendOrderStoragePeriodEvent;
import com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect;
import com.cdek.storage.infrastructure.exceptions.NotFoundOrderStorageException;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Slf4j
public class ApiSendOrderStoragePeriodEventController implements ApiSendOrderStoragePeriodEvent {

    private final OrderStoragePsqlRepository orderStorageRepository;
    private final OrderStorageEventPublisher eventPublisher;

    @Override
    @GetMapping("/send-order-storage-period-event-by-order-number")
    @ControllerParameterAspect.Applied
    public void sendOrderStoragePeriodEventByOrderNumber(@RequestParam final String orderNumber) {
        var orderStorage = Optional.of(orderNumber)
                .map(orderStorageRepository::findOrderStorageByOrderNumber)
                .orElseThrow(NotFoundOrderStorageException::new);

        eventPublisher.publish(orderStorage);
    }

    @Override
    @GetMapping("/send-order-storage-period-event-by-order-uuid")
    @ControllerParameterAspect.Applied
    public void sendOrderStoragePeriodEventByOrderUuid(@RequestParam final String orderUuid) {
        var orderStorage = Optional.of(orderUuid)
                .map(orderStorageRepository::findOrderStorageByOrderUuid)
                .orElseThrow(NotFoundOrderStorageException::new);

        eventPublisher.publish(orderStorage);
    }
}
