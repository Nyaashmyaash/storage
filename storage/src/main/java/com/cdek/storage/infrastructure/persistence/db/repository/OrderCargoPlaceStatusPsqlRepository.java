package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.OrderCargoPlaceStatusRepository;
import com.cdek.storage.buffer.ports.output.OrderCargoPlaceStatusBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.OrderCargoPlaceStatusMapper;
import com.cdek.storage.model.order.CargoPlaceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCargoPlaceStatusPsqlRepository
        implements OrderCargoPlaceStatusBufferRepository, OrderCargoPlaceStatusRepository {

    private final OrderCargoPlaceStatusMapper orderCargoPlaceStatusMapper;

    @Override
    public void saveNewStatus(@Nonnull CargoPlaceStatus cargoPlaceStatus) {
        orderCargoPlaceStatusMapper.insert(cargoPlaceStatus);
    }

    @Nullable
    @Override
    public CargoPlaceStatus findStatusByUuid(@Nonnull String cargoPlaceStatusUuid) {
        return orderCargoPlaceStatusMapper.findStatusByUuid(cargoPlaceStatusUuid);
    }

    @Nullable
    @Override
    public String findCurrentStatusCodeByPackageUuid(@Nonnull String packageUuid) {
        return orderCargoPlaceStatusMapper.findCurrentStatusCodeByPackageUuid(packageUuid);
    }

    @Nullable
    @Override
    public CargoPlaceStatus findCurrentStatusByPackageUuid(@Nonnull String packageUuid) {
        return orderCargoPlaceStatusMapper.findCurrentStatusByPackageUuid(packageUuid);
    }

    @Nonnull
    @Override
    public String getCurrentOfficeUuidByOrderUuid(@Nonnull String orderUuid) {
        return orderCargoPlaceStatusMapper.getCurrentOfficeUuidByOrderUuid(orderUuid);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String cargoPlaceStatusUuid) {
        return orderCargoPlaceStatusMapper.getTimestamp(cargoPlaceStatusUuid);
    }

    @Override
    public void updateStatus(@Nonnull CargoPlaceStatus cargoPlaceStatus) {
        orderCargoPlaceStatusMapper.update(cargoPlaceStatus);
    }

    @Nonnull
    @Override
    public List<String> getAllStatusListByPackageUuidAndStatus(@Nonnull String packageUuid, @Nonnull String status) {
        return orderCargoPlaceStatusMapper.getAllStatusListByPackageUuidAndStatus(packageUuid, status);
    }
}
