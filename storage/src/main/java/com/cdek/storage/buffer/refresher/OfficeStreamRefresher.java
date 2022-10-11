package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.OfficeRefresher;
import com.cdek.storage.buffer.ports.output.OfficeBufferRepository;
import com.cdek.storage.infrastructure.converter.office.OfficeConverter;
import com.cdek.storage.infrastructure.stream.dto.OfficeStreamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfficeStreamRefresher implements OfficeRefresher {

    private final OfficeConverter converter;
    private final OfficeBufferRepository officeRepository;

    @Override
    public void refreshIfNeeded(@Nonnull OfficeStreamDto newDto, @Nullable Instant current) {
        final var office = converter.fromDto(newDto);
        if (current != null) {
            officeRepository.updateOffice(office);
            log.info("Office, with uuid: {} successfully updated.", office.getOfficeUuid());
        } else {
            officeRepository.saveNewOffice(office);
            log.info("Successfully save Office, with uuid: {}.", office.getOfficeUuid());
        }
    }
}
