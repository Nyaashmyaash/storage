package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.buffer.ports.output.LogisticCityBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.LogisticCityMapper;
import com.cdek.storage.model.logistic.LogisticCity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class LogisticCityPsqlRepository implements LogisticCityBufferRepository {

    private final LogisticCityMapper cityMapper;

    @Override
    public void saveNewLogisticCity(@Nonnull LogisticCity logisticCity) {
        cityMapper.save(logisticCity);
    }

    @Override
    public void updateLogisticCity(@Nonnull LogisticCity logisticCity) {
        cityMapper.update(logisticCity);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String logisticCityUuid) {
        return cityMapper.getTimestamp(logisticCityUuid);
    }

    @Nonnull
    public LogisticCity getLogisticCity(@Nonnull String logisticCityCode) {
        return cityMapper.getLogisticCity(logisticCityCode);
    }
}
