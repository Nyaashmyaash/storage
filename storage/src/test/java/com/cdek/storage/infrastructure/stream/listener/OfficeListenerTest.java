package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.OfficeRefresher;
import com.cdek.storage.buffer.ports.output.OfficeBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OfficeStreamDto;
import com.cdek.storage.utils.OfficeTestUtils;
import com.cdek.stream.lib.MessageProcessorProperties;
import com.cdek.stream.lib.StreamLibAutoConfiguration;
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
class OfficeListenerTest {

    @MockBean
    OfficeRefresher refresher;
    @MockBean
    OfficeBufferRepository repository;

    OfficeListener listener;

    @BeforeEach
    void before(@Autowired MessageProcessorProperties properties) {
        listener = new OfficeListener(properties, repository, refresher);
    }

    @Test
    void isValidMessage_ValidMessage_ReturnTrue() {
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDto());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void isValidMessage_MessageWithoutUuid_ReturnFalse() {
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDtoWithoutUuid());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }


    @Test
    void getCurrent_NotNullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(OfficeTestUtils.TIMESTAMP);
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNotNull().isEqualTo(OfficeTestUtils.TIMESTAMP);
    }

    @Test
    void getCurrent_NullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(null);
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNull();
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDto());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDtoWithoutUuid());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void processMessage_Message() {
        Message<OfficeStreamDto> message = new GenericMessage<>(OfficeTestUtils.createOfficeEsbEventDto());
        listener.processMessage(message, Instant.now());

        Mockito.verify(refresher, Mockito.times(1)).refreshIfNeeded(Mockito.any(), Mockito.any());
    }
}
