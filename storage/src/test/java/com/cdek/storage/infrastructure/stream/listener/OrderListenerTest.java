package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.OrderRefresher;
import com.cdek.storage.buffer.ports.output.OrderBufferRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderStreamDto;
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
class OrderListenerTest {

    @MockBean
    OrderRefresher refresher;
    @MockBean
    OrderBufferRepository repository;

    OrderListener listener;

    @BeforeEach
    void before(@Autowired MessageProcessorProperties properties) {
        listener = new OrderListener(properties, repository, refresher);
    }

    @Test
    void isValidMessage_ValidMessage_ReturnTrue() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDto());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void isValidMessage_MessageWithoutOrderUuid_ReturnFalse() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDtoWithoutUuid());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void isValidMessage_MessageWithoutOrderNumber_ReturnFalse() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDtoWithoutNumber());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void isValidMessage_MessageWithoutPackageId_ReturnFalse() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createPackageStreamDtoWithoutPackageId());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void getCurrent_NotNullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(OrderTestUtils.TIMESTAMP);
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNotNull().isEqualTo(OrderTestUtils.TIMESTAMP);
    }

    @Test
    void getCurrent_NullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(null);
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDto());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNull();
    }

    @Test
    void processMessage_Message() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDto());
        listener.processMessage(message, Instant.now());

        Mockito.verify(refresher, Mockito.times(1)).refreshIfNeeded(Mockito.any(), Mockito.any());
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDto());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<OrderStreamDto> message = new GenericMessage<>(OrderTestUtils.createOrderStreamDtoWithoutUuid());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isFalse();
    }
}
