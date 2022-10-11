package com.cdek.storage.buffer.refresher;

import com.cdek.contract.esb.client.ContractEventDto;
import com.cdek.storage.buffer.ports.input.ContractRefresher;
import com.cdek.storage.buffer.ports.output.ContractBufferRepository;
import com.cdek.storage.buffer.ports.output.SellerBufferRepository;
import com.cdek.storage.buffer.refresher.helper.SellerComparerHelper;
import com.cdek.storage.infrastructure.converter.contract.ContractConverter;
import com.cdek.storage.infrastructure.stream.listener.ContractListener;
import com.cdek.storage.model.contract.Contract;
import com.cdek.storage.model.contract.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractStreamRefresher implements ContractRefresher {

    private final ContractConverter converter;
    private final ContractBufferRepository contractRepository;
    private final SellerBufferRepository sellerRepository;
    private final SellerComparerHelper sellerComparerHelper;

    /**
     * Метод проверяет нужно ли обновить существующий договор, либо сохранить новый.
     * Логика работы:
     * 1) если current != null, то это не новый договор(мы нашли в бд предыдущий таймстамп),
     * поэтому договор нужно обновить (обновляются и продавцы, если контракт типа ИМ).
     * 2) иначе договор новый и требует сохранения в локальной бд (сохраняются и продавцы, если контракт типа ИМ).
     * <p>
     * Валидность сообщения проверяется в {@link ContractListener}
     *
     * @param newDto  новое сообщение с шины\кролика.
     * @param current таймстамп из бд.
     */
    @Transactional
    @Override
    public void refreshIfNeeded(@Nonnull ContractEventDto newDto, @Nullable Instant current) {
        var contract = converter.fromDto(newDto);

        if (current != null) {
            Optional.of(contract)
                    .map(Contract::getSellers)
                    .ifPresentOrElse(
                            sellers -> this.updateContractAndSellers(contract, sellers),
                            () -> contractRepository.updateContract(contract)
                    );

            log.info("Contract, with uuid:{} successfully updated.", contract.getContractUuid());

        } else {
            Optional.of(contract)
                    .map(Contract::getSellers)
                    .ifPresentOrElse(
                            sellers -> this.saveContractAndSellers(contract, sellers),
                            () -> contractRepository.saveNewContract(contract)
                    );

            log.info("Successfully save contract, with uuid:{}.", contract.getContractUuid());
        }
    }

    private void saveContractAndSellers(@Nonnull Contract contract, @Nonnull List<Seller> sellers) {
        contractRepository.saveNewContract(contract);
        sellers.forEach(sellerRepository::saveOrUpdateSeller);
    }

    private void updateContractAndSellers(@Nonnull Contract contract, @Nonnull List<Seller> sellers) {
        contractRepository.updateContract(contract);
        sellerComparerHelper.compareSellers(sellerRepository.getAllSellerId(contract.getContractUuid()), sellers);
    }
}
