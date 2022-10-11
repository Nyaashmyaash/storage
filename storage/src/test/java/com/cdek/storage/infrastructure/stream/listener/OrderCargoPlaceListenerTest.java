package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.OrderCargoPlaceStatusRefresher;
import com.cdek.storage.buffer.ports.output.OrderCargoPlaceStatusBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderCargoPlaceStreamDto;
import com.cdek.storage.utils.OrderCargoPlaceTestUtils;
import com.cdek.storage.utils.OrderTestUtils;
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
class OrderCargoPlaceListenerTest {

    @MockBean
    OrderCargoPlaceStatusRefresher refresher;
    @MockBean
    OrderCargoPlaceStatusBufferRepository repository;

    OrderCargoPlaceListener listener;

    @BeforeEach
    void before(@Autowired MessageProcessorProperties properties) {
        listener = new OrderCargoPlaceListener(properties, refresher, repository);
    }

    @Test
    void isValidMessage_ValidMessage_ReturnTrue() {
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void isValidMessage_MessageWithoutUuid_ReturnFalse() {
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEventWithoutUuid());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEventWithoutUuid());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void processMessage_Message() {
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent());
        listener.processMessage(message, Instant.now());

        Mockito.verify(refresher, Mockito.times(1)).saveIfNeeded(Mockito.any(), Mockito.any());
    }

    @Test
    void getCurrent_NotNullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(OrderTestUtils.TIMESTAMP);
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNotNull().isEqualTo(OrderTestUtils.TIMESTAMP);
    }

    @Test
    void getCurrent_NullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(null);
        Message<OrderCargoPlaceStreamDto> message =
                new GenericMessage<>(OrderCargoPlaceTestUtils.createOrderCargoPlaceStatusEvent());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNull();
    }
}
