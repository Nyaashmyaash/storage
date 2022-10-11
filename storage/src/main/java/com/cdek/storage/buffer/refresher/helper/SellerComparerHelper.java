package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.buffer.ports.output.SellerBufferRepository;
import com.cdek.storage.model.contract.Seller;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для сравнения продавцов в договоре: сравнивает список старых продавцов (которые лежат в БД) со списком новых,
 * которые пришли с шины.
 */
@Component
@RequiredArgsConstructor
public class SellerComparerHelper {

    private final SellerBufferRepository sellerBufferRepository;

    /**
     * <p>1) Берем список новых продавцов <code>newSellerList</code> и сохраняем/обновляем всех продавцов
     * из этого списка.</p>
     * <p>2) Создаем список <code>newSellerIdList</code> состоящий из идентификаторов продавцов,
     * которые взяли из списка <code>newSellerList</code>.</p>
     * <p>3) Создаем новый список <code>sellerListToDelete</code>, состоящий из идентификаторов продавцов,
     * которых нет в новом списке <code>newSellerIdList</code>, и удаляем этих продавцов.</p>
     *
     * @param oldSellerIdList список идентификаторов продавцов уже хранящихся в бд.
     * @param newSellerList   список новый продавцов полученных с шины.
     */
    public void compareSellers(@Nonnull List<Long> oldSellerIdList, @Nonnull List<Seller> newSellerList) {

        newSellerList.forEach(sellerBufferRepository::saveOrUpdateSeller);

        final List<Long> newSellerIdList = newSellerList.stream()
                .map(Seller::getId)
                .collect(Collectors.toList());

        final List<Long> sellerListToDelete = oldSellerIdList.stream()
                .filter(sellerId -> !newSellerIdList.contains(sellerId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sellerListToDelete)) {
            sellerBufferRepository.deleteSellerList(sellerListToDelete);
        }
    }
}
