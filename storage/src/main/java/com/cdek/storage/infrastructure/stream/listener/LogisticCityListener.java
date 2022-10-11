package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.locality.client.api.dto.CityEsbDto;
import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.storage.buffer.ports.input.LogisticCityRefresher;
import com.cdek.storage.buffer.ports.output.LogisticCityBufferRepository;
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
public class LogisticCityListener extends ExchangeObjectMessageProcessor<CityEsbDto, Instant> {

    private final LogisticCityBufferRepository logisticCityRepository;
    private final LogisticCityRefresher refresher;

    public LogisticCityListener(MessageProcessorProperties properties,
            LogisticCityBufferRepository logisticCityRepository,
            LogisticCityRefresher refresher) {
        super(properties, Function.identity());
        this.logisticCityRepository = logisticCityRepository;
        this.refresher = refresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<CityEsbDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .isPresent();
    }

    @Override
    protected boolean processValidation(@Nonnull Message<CityEsbDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info("Invalid CityEsbDto, cause UUID: {}.", dto.getUuid());
            return false;
        }
        return true;
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<CityEsbDto> message) {
        return logisticCityRepository.getTimestamp(message.getPayload().getUuid().toString());
    }

    @Override
    protected void processMessage(@Nonnull Message<CityEsbDto> message, Instant current) {
        refresher.refreshIfNeeded(message.getPayload(), current);
    }
}
