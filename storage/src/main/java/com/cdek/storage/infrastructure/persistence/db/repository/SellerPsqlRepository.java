package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.SellerRepository;
import com.cdek.storage.buffer.ports.output.SellerBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.SellerMapper;
import com.cdek.storage.model.contract.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SellerPsqlRepository implements SellerBufferRepository, SellerRepository {

    private final SellerMapper sellerMapper;

    @Override
    public void saveOrUpdateSeller(@Nonnull Seller seller) {
        sellerMapper.insertOrUpdateSeller(seller);
    }

    @Nullable
    @Override
    public Seller findSellerById(@Nonnull Long sellerId) {
        return sellerMapper.findSellerById(sellerId);
    }

    @Nonnull
    @Override
    public List<Seller> findSellerListByContractUuid(@Nonnull String contractUuid) {
        return sellerMapper.findSellerListByContractUuid(contractUuid);
    }

    @Nonnull
    @Override
    public List<Long> getAllSellerId(@Nonnull String contractUuid) {
        return sellerMapper.getAllSellerId(contractUuid);
    }

    @Override
    public void deleteSellerList(@Nonnull List<Long> sellerIdList) {
        sellerMapper.deleteSellerList(sellerIdList);
    }
}
