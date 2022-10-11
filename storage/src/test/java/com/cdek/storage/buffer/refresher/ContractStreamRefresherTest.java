package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.ContractRefresher;
import com.cdek.storage.buffer.ports.output.ContractBufferRepository;
import com.cdek.storage.buffer.ports.output.SellerBufferRepository;
import com.cdek.storage.buffer.refresher.helper.SellerComparerHelper;
import com.cdek.storage.infrastructure.converter.contract.ContractConverter;
import com.cdek.storage.utils.ContractTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ContractStreamRefresherTest {
    @MockBean
    ContractConverter converter;
    @MockBean
    ContractBufferRepository contractRepository;
    @MockBean
    SellerBufferRepository sellerRepository;
    @MockBean
    SellerComparerHelper sellerComparerHelper;

    ContractRefresher refresher;

    @BeforeEach
    void before() {
        refresher = new ContractStreamRefresher(converter, contractRepository, sellerRepository, sellerComparerHelper);
        Mockito.clearInvocations(converter, contractRepository, sellerRepository, sellerComparerHelper);
    }

    @Test
    void refreshIfNeeded_NewContractTypeClient_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(ContractTestUtils.createContractTypeClientModel());
        Mockito.doNothing().when(contractRepository).saveNewContract(Mockito.any());
        Mockito.doNothing().when(sellerComparerHelper).compareSellers(Mockito.any(), Mockito.anyList());

        refresher.refreshIfNeeded(ContractTestUtils.createContractEventDtoTypeClient(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(contractRepository, Mockito.times(1)).saveNewContract(Mockito.any());
        Mockito.verify(sellerRepository, Mockito.never()).saveOrUpdateSeller(Mockito.any());
        Mockito.verify(contractRepository, Mockito.never()).updateContract(Mockito.any());
    }

    @Test
    void refreshIfNeeded_UpdatedContractTypeClient_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(ContractTestUtils.createContractTypeClientModel());
        Mockito.doNothing().when(contractRepository).updateContract(Mockito.any());
        Mockito.doNothing().when(sellerComparerHelper).compareSellers(Mockito.any(), Mockito.anyList());

        refresher.refreshIfNeeded(ContractTestUtils.createContractEventDtoTypeClient(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(contractRepository, Mockito.never()).saveNewContract(Mockito.any());
        Mockito.verify(contractRepository, Mockito.times(1)).updateContract(Mockito.any());
    }

    @Test
    void refreshIfNeeded_NewContractTypeIM_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(ContractTestUtils.createContractIMModel());
        Mockito.doNothing().when(contractRepository).saveNewContract(Mockito.any());
        Mockito.doNothing().when(sellerComparerHelper).compareSellers(Mockito.any(), Mockito.anyList());

        refresher.refreshIfNeeded(ContractTestUtils.createContractEventDtoTypeIM(), null);

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(contractRepository, Mockito.times(1)).saveNewContract(Mockito.any());
        Mockito.verify(sellerRepository, Mockito.times(1)).saveOrUpdateSeller(Mockito.any());
        Mockito.verify(contractRepository, Mockito.never()).updateContract(Mockito.any());
    }

    @Test
    void refreshIfNeeded_UpdatedContractTypeIM_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(ContractTestUtils.createContractIMModel());
        Mockito.doNothing().when(contractRepository).updateContract(Mockito.any());
        Mockito.doNothing().when(sellerComparerHelper).compareSellers(Mockito.any(), Mockito.anyList());

        refresher.refreshIfNeeded(ContractTestUtils.createContractEventDtoTypeIM(), Instant.now());

        Mockito.verify(converter, Mockito.times(1)).fromDto(Mockito.any());
        Mockito.verify(contractRepository, Mockito.never()).saveNewContract(Mockito.any());
        Mockito.verify(contractRepository, Mockito.times(1)).updateContract(Mockito.any());
        Mockito.verify(sellerRepository, Mockito.times(1)).getAllSellerId(Mockito.any());
    }
}
