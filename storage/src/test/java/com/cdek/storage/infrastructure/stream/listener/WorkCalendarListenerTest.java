package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.WorkCalendarRefresher;
import com.cdek.storage.buffer.ports.output.WorkCalendarBufferRepository;
import com.cdek.storage.utils.ContractTestUtils;
import com.cdek.storage.utils.WorkCalendarTestUtils;
import com.cdek.stream.lib.MessageProcessorProperties;
import com.cdek.stream.lib.StreamLibAutoConfiguration;
import com.cdek.work.calendar.client.dto.CalendarEsbDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@AutoConfigureJson
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(value = { StreamLibAutoConfiguration.class, ScalableLocksAutoConfiguration.class,
        LocalScalableLocksAutoConfiguration.class })
class WorkCalendarListenerTest {

    @MockBean
    WorkCalendarRefresher refresher;
    @MockBean
    WorkCalendarBufferRepository repository;

    WorkCalendarListener listener;

    @BeforeEach
    void setUp(@Autowired MessageProcessorProperties properties) {
        listener = new WorkCalendarListener(properties, repository, refresher);
    }

    @Test
    void isValidMessage_ValidContractModel_ReturnTrue() {
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDto());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void isValidMessage_NotValidContractModelWithoutUuid_ReturnFalse() {
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDtoWithoutUuid());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void getCurrent_NotNullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString()))
                .thenReturn(ContractTestUtils.TIMESTAMP);
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNotNull().isEqualTo(ContractTestUtils.TIMESTAMP);
    }

    @Test
    void getCurrent_NullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(null);
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNull();
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDto());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDtoWithoutUuid());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void processMessage_Message() {
        Message<CalendarEsbDto> message = new GenericMessage<>(WorkCalendarTestUtils.crateCalendarEsbDto());
        listener.processMessage(message, Instant.now());

        Mockito.verify(refresher, Mockito.times(1)).refreshIfNeeded(Mockito.any(), Mockito.any());
    }
}
