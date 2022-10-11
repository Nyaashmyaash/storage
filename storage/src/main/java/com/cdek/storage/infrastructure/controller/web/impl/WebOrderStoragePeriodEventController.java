package com.cdek.storage.infrastructure.controller.web.impl;

import com.cdek.storage.client.esb.OrderStorageEventDto;
import com.cdek.storage.infrastructure.controller.web.WebOrderStoragePeriodEvent;
import com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect;
import com.cdek.storage.infrastructure.converter.storage.OrderStorageEventDtoConverter;
import com.cdek.storage.infrastructure.exceptions.NotFoundEventException;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebOrderStoragePeriodEventController implements WebOrderStoragePeriodEvent {

    private final OrderStoragePsqlRepository orderStorageRepository;
    private final OrderStorageEventDtoConverter converter;

    @Override
    @GetMapping("/web/storage/get-order-storage-period-event-by-order-number")
    @ControllerParameterAspect.Applied
    public ResponseEntity<OrderStorageEventDto> getOrderStoragePeriodEventByOrderNumber(
            @RequestParam final String orderNumber) {
        return Optional.of(orderNumber)
                .map(orderStorageRepository::findOrderStorageByOrderNumber)
                .map(converter::fromOrderStorage)
                .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElseThrow(NotFoundEventException::new);
    }

    @Override
    @GetMapping("/web/storage/get-order-storage-period-event-by-order-uuid")
    @ControllerParameterAspect.Applied
    public ResponseEntity<OrderStorageEventDto> getOrderStoragePeriodEventByOrderUuid(
            @RequestParam final String orderUuid) {
        return Optional.of(orderUuid)
                .map(orderStorageRepository::findOrderStorageByOrderUuid)
                .map(converter::fromOrderStorage)
                .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElseThrow(NotFoundEventException::new);
    }
}
