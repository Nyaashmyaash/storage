package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.LogisticCityRefresher;
import com.cdek.storage.buffer.ports.output.LogisticCityBufferRepository;
import com.cdek.storage.infrastructure.converter.logistic.LogisticCityConverter;
import com.cdek.storage.utils.LogisticCityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class LogisticCityStreamRefresherTest {

    @MockBean
    LogisticCityConverter converter;
    @MockBean
    LogisticCityBufferRepository repository;

    LogisticCityRefresher refresher;

    @BeforeEach
    void setUp() {
        refresher = new LogisticCityStreamRefresher(converter, repository);

        Mockito.clearInvocations(converter, repository);
    }

    @Test
    void refreshIfNeeded_NewLogisticCity_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(LogisticCityTestUtils.createLogisticCityModel());
        Mockito.doNothing().when(repository).saveNewLogisticCity(Mockito.any());

        refresher.refreshIfNeeded(LogisticCityTestUtils.createCityEsbDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).saveNewLogisticCity(Mockito.any());
        Mockito.verify(repository, Mockito.never()).updateLogisticCity(Mockito.any());
    }

    @Test
    void refreshIfNeeded_UpdatedLogisticCity_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(LogisticCityTestUtils.createLogisticCityModel());
        Mockito.doNothing().when(repository).updateLogisticCity(Mockito.any());

        refresher.refreshIfNeeded(LogisticCityTestUtils.createCityEsbDto(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).updateLogisticCity(Mockito.any());
        Mockito.verify(repository, Mockito.never()).saveNewLogisticCity(Mockito.any());
    }
}
