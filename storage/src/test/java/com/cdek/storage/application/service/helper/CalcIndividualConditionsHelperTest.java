package com.cdek.storage.application.service.helper;

import com.cdek.catalog.common.entity.OrderType;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.output.ContractRepository;
import com.cdek.storage.application.ports.output.SellerRepository;
import com.cdek.storage.utils.ContractTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.Assertions;
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
class CalcIndividualConditionsHelperTest {

    @MockBean
    SellerRepository sellerRepository;
    @MockBean
    ContractRepository contractRepository;

    CalcIndividualConditionsHelper calcIndividualConditionsHelper;

    @BeforeEach
    void before() {
        calcIndividualConditionsHelper = new CalcIndividualConditionsHelper(sellerRepository, contractRepository);
        Mockito.clearInvocations(sellerRepository, contractRepository);
    }

    @Test
    void getIndividualConditionsForFreeStoragePeriod_secondSellerExistInDB() {
        Mockito.when(contractRepository.getContractUuidByContractNumber(Mockito.anyString()))
                .thenReturn(OrderTestUtils.PAYER_CONTRACT_UUID.toString());
        Mockito.when(sellerRepository.findSellerListByContractUuid(Mockito.anyString()))
                .thenReturn(ContractTestUtils.getSellersList());

        int actual = calcIndividualConditionsHelper
                .getIndividualConditionsForFreeStoragePeriod(OrderTestUtils.createOrderModel());

        Assertions.assertEquals(30, actual);
    }

    @Test
    void getIndividualConditionsForFreeStoragePeriod_sellerNameInOrderIsEmpty() {
        Mockito.when(contractRepository.getContractUuidByContractNumber(Mockito.anyString()))
                .thenReturn(OrderTestUtils.PAYER_CONTRACT_UUID.toString());
        Mockito.when(sellerRepository.findSellerListByContractUuid(Mockito.anyString()))
                .thenReturn(ContractTestUtils.getSellersList());

        int actual = calcIndividualConditionsHelper
                .getIndividualConditionsForFreeStoragePeriod(OrderTestUtils.createOrderModelWithEmptySellerName());

        Assertions.assertEquals(20, actual);
    }

    @Test
    void getIndividualConditionsForFreeStoragePeriod_secondSellerNotExistInDB() {
        Mockito.when(contractRepository.getContractUuidByContractNumber(Mockito.anyString()))
                .thenReturn(OrderTestUtils.PAYER_CONTRACT_UUID.toString());
        Mockito.when(sellerRepository.findSellerListByContractUuid(Mockito.anyString()))
                .thenReturn(ContractTestUtils.getSellersList());

        int actual = calcIndividualConditionsHelper
                .getIndividualConditionsForFreeStoragePeriod(OrderTestUtils.createOrderModelWithRandomSellerName());

        Assertions.assertEquals(10, actual);
    }

    @Test
    void getIndividualConditionsForFreeStoragePeriod_secondSellerAndDefaultSellerNotExistInDB() {
        Mockito.when(contractRepository.getContractUuidByContractNumber(Mockito.anyString()))
                .thenReturn(OrderTestUtils.PAYER_CONTRACT_UUID.toString());
        Mockito.when(sellerRepository.findSellerListByContractUuid(Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        int actual = calcIndividualConditionsHelper
                .getIndividualConditionsForFreeStoragePeriod(OrderTestUtils.createOrderModelWithRandomSellerName());

        Assertions.assertEquals(0, actual);
    }

    @Test
    void getIndividualConditionsForFreeStoragePeriod_OrderTypeDelivery() {
        Mockito.when(contractRepository.getContractUuidByContractNumber(Mockito.anyString()))
                .thenReturn(OrderTestUtils.PAYER_CONTRACT_UUID.toString());
        Mockito.when(sellerRepository.findSellerListByContractUuid(Mockito.anyString()))
                .thenReturn(ContractTestUtils.getSellersList());

        int actual = calcIndividualConditionsHelper
                .getIndividualConditionsForFreeStoragePeriod(
                        OrderTestUtils.getOrder(TariffMode.TARIFF_MODE_DP, 0, OrderType.ORDER_TYPE_DELIVERY));

        Assertions.assertEquals(0, actual);
    }
}
