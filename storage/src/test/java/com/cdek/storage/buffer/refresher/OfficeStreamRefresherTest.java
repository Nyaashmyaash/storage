package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.OfficeRefresher;
import com.cdek.storage.buffer.ports.output.OfficeBufferRepository;
import com.cdek.storage.infrastructure.converter.office.OfficeConverter;
import com.cdek.storage.utils.OfficeTestUtils;
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
class OfficeStreamRefresherTest {

    @MockBean
    OfficeConverter converter;
    @MockBean
    OfficeBufferRepository repository;

    OfficeRefresher refresher;

    @BeforeEach
    void setUp() {
        refresher = new OfficeStreamRefresher(converter, repository);

        Mockito.clearInvocations(converter, repository);
    }

    @Test
    void refreshIfNeeded_NewOffice_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OfficeTestUtils.createOfficeModel());
        Mockito.doNothing().when(repository).saveNewOffice(Mockito.any());

        refresher.refreshIfNeeded(OfficeTestUtils.createOfficeEsbEventDto(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).saveNewOffice(Mockito.any());
        Mockito.verify(repository, Mockito.never()).updateOffice(Mockito.any());
    }

    @Test
    void refreshIfNeeded_UpdatedOffice_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(OfficeTestUtils.createOfficeModel());
        Mockito.doNothing().when(repository).updateOffice(Mockito.any());

        refresher.refreshIfNeeded(OfficeTestUtils.createOfficeEsbEventDto(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(repository, Mockito.times(1)).updateOffice(Mockito.any());
        Mockito.verify(repository, Mockito.never()).saveNewOffice(Mockito.any());
    }
}
