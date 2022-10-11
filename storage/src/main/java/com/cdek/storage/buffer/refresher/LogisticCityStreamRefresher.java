package com.cdek.storage.buffer.refresher;

import com.cdek.locality.client.api.dto.CityEsbDto;
import com.cdek.storage.buffer.ports.input.LogisticCityRefresher;
import com.cdek.storage.buffer.ports.output.LogisticCityBufferRepository;
import com.cdek.storage.infrastructure.converter.logistic.LogisticCityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogisticCityStreamRefresher implements LogisticCityRefresher {

    private final LogisticCityConverter converter;
    private final LogisticCityBufferRepository logisticCityRepository;

    @Override
    public void refreshIfNeeded(@Nonnull CityEsbDto newDto, @Nullable Instant current) {
        final var logisticCity = converter.fromDto(newDto);
        if (current != null) {
            logisticCityRepository.updateLogisticCity(logisticCity);
            log.info("LogisticCity, with uuid: {} successfully updated.", logisticCity.getCityUuid());
        } else {
            logisticCityRepository.saveNewLogisticCity(logisticCity);
            log.info("Successfully save LogisticCity, with uuid: {}.", logisticCity.getCityUuid());
        }
    }
}
