package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.PackageRepository;
import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.PackageMapper;
import com.cdek.storage.model.order.Package;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PackagePsqlRepository implements PackageBufferRepository, PackageRepository {

    private final PackageMapper packageMapper;

    @Override
    public void saveOrUpdatePackage(@Nonnull Package pack) {
        packageMapper.insertOrUpdatePackage(pack);
    }

    @Nullable
    @Override
    public Package findPackageByUuid(@Nonnull String packageUuid) {
        return packageMapper.findPackageByUuid(packageUuid);
    }

    @Nonnull
    @Override
    public List<Package> getNotDeletedPackagesByOrderUuid(@Nonnull String orderUuid) {
        return packageMapper.getNotDeletedPackagesByOrderUuid(orderUuid);
    }

    @Nullable
    @Override
    public List<Package> findAllPackagesByOrderUuid(@Nonnull String orderUuid) {
        return packageMapper.findAllPackagesByOrderUuid(orderUuid);
    }

    @Override
    public void setPackagesIsDeleted(@Nonnull List<String> packUuidList) {
        packageMapper.setPackagesIsDeleted(packUuidList);
    }

    @Override
    public void setAllPackagesIsDeleted(@Nonnull String orderUuid) {
        packageMapper.setAllPackagesIsDeleted(orderUuid);
    }

    @Nonnull
    @Override
    public List<String> getAllNotDeletedPackageUuids(@Nonnull String orderUuid) {
        return packageMapper.getAllNotDeletedPackageUuids(orderUuid);
    }
}
