package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.ContractRepository;
import com.cdek.storage.buffer.ports.output.ContractBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.ContractMapper;
import com.cdek.storage.model.contract.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class ContractPsqlRepository implements ContractBufferRepository, ContractRepository {

    private final ContractMapper contractMapper;

    @Override
    public void saveNewContract(@Nonnull Contract contract) {
        contractMapper.insert(contract);
    }

    @Override
    public void updateContract(@Nonnull Contract contract) {
        contractMapper.update(contract);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String contractUuid) {
        return contractMapper.getTimestamp(contractUuid);
    }

    @Nullable
    @Override
    public Contract findContractByUuid(@Nonnull String contractUuid) {
        return contractMapper.findContractByUuid(contractUuid);
    }

    @Nonnull
    @Override
    public String getContractUuidByContractNumber(@Nonnull String contractNumber) {
        return contractMapper.getContractUuidByContractNumber(contractNumber);
    }
}
