package com.cdek.storage.utils;

import com.cdek.contract.esb.client.ContractEventDto;
import com.cdek.contract.esb.client.seller.RefundRuleDto;
import com.cdek.contract.esb.client.seller.SellerDto;
import com.cdek.storage.model.contract.Contract;
import com.cdek.storage.model.contract.Seller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ContractTestUtils {

    public static final UUID CONTRACT_UUID = UUID.randomUUID();
    public static final UUID CONTRAGENT_UUID = UUID.randomUUID();
    public static final Long CONTRACT_ID = 1L;
    public static final Long SELLER_ID_1 = 2L;
    public static final Long SELLER_ID_2 = 3L;
    public static final Long SELLER_ID_3 = 4L;
    public static final int FREE_STORAGE_DAYS = 30;
    public static final int FREE_STORAGE_DAYS_FOR_POSTAMAT = 40;
    public static final String CONTRACT_NUMBER = "contractNumber";
    public static final String TYPE_CODE_INTERNET_STORE = "3";
    public static final String TYPE_CODE_CLIENT = "1";
    public static final String STATUS_CODE = "1";
    public static final String UPDATED_STATUS_CODE = "2";
    public static final String SELLER_NAME = "+someSeller-Name!";
    public static final String UPDATED_SELLER_NAME = "updatedSellerName";
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    public static Contract createContractIMModel() {
        return Contract.builder()
                .contractUuid(CONTRACT_UUID.toString())
                .id(CONTRACT_ID)
                .number(CONTRACT_NUMBER)
                .typeCode(TYPE_CODE_INTERNET_STORE)
                .statusCode(STATUS_CODE)
                .contragentUuid(CONTRAGENT_UUID.toString())
                .sellers(Collections.singletonList(createSellerModel1()))
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Contract createContractForDb() {
        Contract updatedContract = createContractIMModel();
        updatedContract.setSellers(Collections.emptyList());

        return updatedContract;
    }

    public static Contract createUpdatedContractForDb() {
        Contract updatedContract = createContractForDb();
        updatedContract.setStatusCode(UPDATED_STATUS_CODE);

        return updatedContract;
    }

    public static Contract createContractTypeClientModel() {
        return Contract.builder()
                .contractUuid(CONTRACT_UUID.toString())
                .id(CONTRACT_ID)
                .number(CONTRACT_NUMBER)
                .typeCode(TYPE_CODE_CLIENT)
                .statusCode(STATUS_CODE)
                .contragentUuid(CONTRAGENT_UUID.toString())
                .timestamp(TIMESTAMP)
                .build();
    }

    public static ContractEventDto createContractEventDtoTypeIM() {
        ContractEventDto dto = ContractEventDto.builder()
                .id(CONTRACT_ID)
                .number(CONTRACT_NUMBER)
                .typeCode(TYPE_CODE_INTERNET_STORE)
                .statusCode(STATUS_CODE)
                .clientUuid(CONTRAGENT_UUID)
                .sellers(Collections.singletonList(createSellerDto()))
                .updateTimestamp(TIMESTAMP)
                .build();
        dto.setUuid(CONTRACT_UUID);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());
        return dto;
    }

    public static ContractEventDto createContractEventDtoTypeClient() {
        ContractEventDto dto = ContractEventDto.builder()
                .id(CONTRACT_ID)
                .number(CONTRACT_NUMBER)
                .typeCode(TYPE_CODE_CLIENT)
                .statusCode(STATUS_CODE)
                .clientUuid(CONTRAGENT_UUID)
                .updateTimestamp(TIMESTAMP)
                .build();
        dto.setUuid(CONTRACT_UUID);
        dto.setTimestamp(TIMESTAMP.toEpochMilli());
        return dto;
    }

    public static ContractEventDto createContractEventDtoWithoutUuid() {
        ContractEventDto dto = createContractEventDtoTypeIM();
        dto.setUuid(null);
        return dto;
    }

    public static SellerDto createSellerDto() {
        RefundRuleDto refundRule = new RefundRuleDto();
        refundRule.setFreeStorageDaysCount(Long.parseLong(String.valueOf(FREE_STORAGE_DAYS)));
        SellerDto dto = SellerDto.builder()
                .id(SELLER_ID_1)
                .name(SELLER_NAME)
                .refundRule(refundRule)
                .postamatOrderStorageDaysCount(Long.parseLong(String.valueOf(FREE_STORAGE_DAYS_FOR_POSTAMAT)))
                .build();

        return dto;
    }

    public static Seller createSellerModel1() {
        return Seller.builder()
                .id(SELLER_ID_1)
                .sellerName(SELLER_NAME)
                .contractUuid(CONTRACT_UUID.toString())
                .freeStorageDaysCount(FREE_STORAGE_DAYS)
                .postamatOrderStorageDaysCount(FREE_STORAGE_DAYS_FOR_POSTAMAT)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Seller createSellerModel2() {
        return Seller.builder()
                .id(SELLER_ID_2)
                .sellerName(null)
                .contractUuid(CONTRACT_UUID.toString())
                .freeStorageDaysCount(20)
                .postamatOrderStorageDaysCount(10)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Seller createSellerModel3() {
        return Seller.builder()
                .id(SELLER_ID_3)
                .sellerName(null)
                .contractUuid(CONTRACT_UUID.toString())
                .freeStorageDaysCount(20)
                .postamatOrderStorageDaysCount(10)
                .timestamp(TIMESTAMP)
                .build();
    }

    public static Seller createUpdatedSellerForDb() {
        Seller seller = createSellerModel1();
        seller.setSellerName(UPDATED_SELLER_NAME);

        return seller;
    }

    public static List<Seller> getSellersList() {
        List<Seller> list = new ArrayList<>();
        list.add(createSellerModel1());
        list.add(createSellerModel2());

        return list;
    }

    public static List<Long> getSellerIdList() {
        List<Long> sellerIdList = new ArrayList<>();
        sellerIdList.add(SELLER_ID_1);
        sellerIdList.add(SELLER_ID_2);

        return sellerIdList;
    }

    public static List<Seller> getSellersListWithFreeSellers() {
        List<Seller> list = new ArrayList<>();
        list.add(createSellerModel1());
        list.add(createSellerModel2());
        list.add(createSellerModel3());

        return list;
    }
}
