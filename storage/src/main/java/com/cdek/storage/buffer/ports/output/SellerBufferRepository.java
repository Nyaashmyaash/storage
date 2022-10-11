package com.cdek.storage.buffer.ports.output;

import com.cdek.storage.model.contract.Seller;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Репозиторий по работе с продавцами для буферизации данных.
 */
public interface SellerBufferRepository {

    /**
     * Сохранить или одновить продавца в бд.
     *
     * @param seller объект {@link Seller}.
     */
    void saveOrUpdateSeller(@Nonnull Seller seller);

    /**
     * Получить список идентификаторов всех продавцов из контракта.
     *
     * @param contractUuid идентификатор контракта.
     */
    List<Long> getAllSellerId(@Nonnull String contractUuid);

    /**
     * Удалить список продавцов.
     *
     * @param sellerIdList список идентификаторов продавцов.
     */
    void deleteSellerList(@Nonnull List<Long> sellerIdList);
}
