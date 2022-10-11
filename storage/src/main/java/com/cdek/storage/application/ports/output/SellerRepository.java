package com.cdek.storage.application.ports.output;

import com.cdek.storage.model.contract.Seller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Репозиторий по работе с продавцами.
 */
public interface SellerRepository {

    /**
     * Найти продавца по идентификатору.
     *
     * @param sellerId идентификатор продавца.
     * @return объект {@link Seller}.
     */
    @Nullable
    Seller findSellerById(@Nonnull Long sellerId);

    /**
     * Найти список продавцов по идентификатору контракта.
     *
     * @param contractUuid идентификатор контракта.
     * @return объект {@link Seller}.
     */
    @Nonnull
    List<Seller> findSellerListByContractUuid(@Nonnull String contractUuid);
}
