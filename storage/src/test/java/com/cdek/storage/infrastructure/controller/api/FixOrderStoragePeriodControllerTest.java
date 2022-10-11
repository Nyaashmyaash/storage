package com.cdek.storage.infrastructure.controller.api;

import com.cdek.abac.pep.ICdekAbac;
import com.cdek.platform.web.http.HttpHeaders;
import com.cdek.storage.infrastructure.service.fix.DeleteDuplicateOrderStoragePeriodService;
import com.cdek.storage.infrastructure.service.fix.FixDateOfReceiptAndDateDeadlineService;
import com.cdek.storage.infrastructure.service.fix.FixOrderStoragePeriodService;
import com.cdek.storage.utils.AuthTestUtils;
import com.cdek.storage.utils.RequestTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import java.time.Instant;

@ActiveProfiles("test")
@WebMvcTest(controllers = FixOrderStoragePeriodController.class)
@ImportAutoConfiguration({ AopAutoConfiguration.class, RestTemplateAutoConfiguration.class })
class FixOrderStoragePeriodControllerTest {

    @MockBean
    ICdekAbac cdekAbac;
    @MockBean
    FixOrderStoragePeriodService fixService;
    @MockBean
    FixDateOfReceiptAndDateDeadlineService fixDateOfReceiptAndDateDeadlineService;
    @MockBean
    DeleteDuplicateOrderStoragePeriodService deleteDuplicateOrderStoragePeriodService;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void beforeTest() {
        Mockito.when(cdekAbac.getUserFullInfo(ArgumentMatchers.anyString()))
                .thenReturn(AuthTestUtils.getValidUser());
    }

    @Test
    void fixOrderStoragePeriod() throws Exception {
        Mockito.doNothing().when(fixService).fixDeadlineOfStoragePeriod(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/storage/fix-storage-period")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.X_AUTH_TOKEN, "test_token")
                    .param("dateFrom", Instant.now().toString())
                    .param("dateTo", Instant.now().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void fixDateOfReceiptAndDateDeadline() throws Exception {
        Mockito.doNothing().when(fixDateOfReceiptAndDateDeadlineService)
                .fixDateOfReceiptAndDateDeadline(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/storage/fix-date-of-receipt-and-date-deadline")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.X_AUTH_TOKEN, "test_token")
                    .content(RequestTestUtils.createFixDateOfReceiptRequest()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
