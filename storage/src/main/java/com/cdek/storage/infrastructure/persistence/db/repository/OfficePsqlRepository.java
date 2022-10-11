package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.buffer.ports.output.OfficeBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.OfficeMapper;
import com.cdek.storage.model.office.Office;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class OfficePsqlRepository implements OfficeBufferRepository {

    private final OfficeMapper mapper;

    @Override
    public void saveNewOffice(@Nonnull Office office) {
        mapper.save(office);
    }

    @Override
    public void updateOffice(@Nonnull Office office) {
        mapper.update(office);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String officeUuid) {
        return mapper.getTimestamp(officeUuid);
    }

    @Nonnull
    public Office getOfficeByUuid(@Nonnull String officeUuid) {
        return mapper.getOfficeByUuid(officeUuid);
    }
}
