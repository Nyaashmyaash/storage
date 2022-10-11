package com.cdek.storage.infrastructure.controller.web;

import com.cdek.abac.pep.ICdekAbac;
import com.cdek.storage.infrastructure.controller.web.impl.WebOrderStoragePeriodEventController;
import com.cdek.storage.infrastructure.converter.storage.OrderStorageEventDtoConverter;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderStoragePsqlRepository;
import com.cdek.storage.utils.OrderStorageTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@WebMvcTest(controllers = WebOrderStoragePeriodEventController.class)
@ImportAutoConfiguration({ AopAutoConfiguration.class, RestTemplateAutoConfiguration.class })
class WebOrderStoragePeriodEventControllerTest {

    @MockBean
    ICdekAbac cdekAbac;
    @MockBean
    OrderStoragePsqlRepository orderStorageRepository;
    @MockBean
    OrderStorageEventDtoConverter converter;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getOrderStoragePeriodEventByOrderNumber() throws Exception {
        Mockito.when(orderStorageRepository.findOrderStorageByOrderNumber(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.when(converter.fromOrderStorage(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageEventDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/web/storage/get-order-storage-period-event-by-order-number")
                .contentType(MediaType.APPLICATION_JSON)
                .param("orderNumber", OrderTestUtils.ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getOrderStoragePeriodEventByOrderUuid() throws Exception {
        Mockito.when(orderStorageRepository.findOrderStorageByOrderUuid(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorage());
        Mockito.when(converter.fromOrderStorage(Mockito.any()))
                .thenReturn(OrderStorageTestUtils.createOrderStorageEventDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/web/storage/get-order-storage-period-event-by-order-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .param("orderUuid", OrderTestUtils.ORDER_UUID.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
