package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.buffer.ports.output.SellerBufferRepository;
import com.cdek.storage.utils.ContractTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class SellerComparerHelperTest {

    @MockBean
    SellerBufferRepository sellerBufferRepository;

    SellerComparerHelper sellerComparerHelper;

    @BeforeEach
    void before() {
        sellerComparerHelper = new SellerComparerHelper(sellerBufferRepository);
        Mockito.clearInvocations(sellerBufferRepository);
    }

    @Test
    void compareSellers_NewSellersListEqualsOldSellersList() {
        Mockito.doNothing().when(sellerBufferRepository).saveOrUpdateSeller(Mockito.any());
        Mockito.doNothing().when(sellerBufferRepository).deleteSellerList(Mockito.anyList());

        sellerComparerHelper.compareSellers(ContractTestUtils.getSellerIdList(), ContractTestUtils.getSellersList());

        Mockito.verify(sellerBufferRepository, Mockito.times(2)).saveOrUpdateSeller(Mockito.any());
        Mockito.verify(sellerBufferRepository, Mockito.never()).deleteSellerList(Mockito.anyList());
    }

    @Test
    void compareSellers_NewSellersListHaveOneNewSeller() {
        Mockito.doNothing().when(sellerBufferRepository).saveOrUpdateSeller(Mockito.any());
        Mockito.doNothing().when(sellerBufferRepository).deleteSellerList(Mockito.anyList());

        sellerComparerHelper
                .compareSellers(ContractTestUtils.getSellerIdList(), ContractTestUtils.getSellersListWithFreeSellers());

        Mockito.verify(sellerBufferRepository, Mockito.times(3)).saveOrUpdateSeller(Mockito.any());
        Mockito.verify(sellerBufferRepository, Mockito.never()).deleteSellerList(Mockito.anyList());
    }

    @Test
    void compareSellers_NewSellersListNotHaveOneSeller() {
        Mockito.doNothing().when(sellerBufferRepository).saveOrUpdateSeller(Mockito.any());
        Mockito.doNothing().when(sellerBufferRepository).deleteSellerList(Mockito.anyList());

        sellerComparerHelper.compareSellers(ContractTestUtils.getSellerIdList(),
                Collections.singletonList(ContractTestUtils.createSellerModel1()));

        Mockito.verify(sellerBufferRepository, Mockito.times(1)).saveOrUpdateSeller(Mockito.any());
        Mockito.verify(sellerBufferRepository, Mockito.times(1)).deleteSellerList(Mockito.anyList());
    }
}
