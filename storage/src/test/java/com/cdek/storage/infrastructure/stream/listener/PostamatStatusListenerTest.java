package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.omnic.integration.client.dto.PostomatEventDto;
import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.PostamatStatusRefresher;
import com.cdek.storage.utils.PostamatStatusTestUtils;
import com.cdek.stream.lib.MessageProcessorProperties;
import com.cdek.stream.lib.StreamLibAutoConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@AutoConfigureJson
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(value = { StreamLibAutoConfiguration.class, ScalableLocksAutoConfiguration.class,
        LocalScalableLocksAutoConfiguration.class })
class PostamatStatusListenerTest {

    @MockBean
    PostamatStatusRefresher statusRefresher;

    PostamatStatusListener statusListener;

    @BeforeEach
    void setUp(@Autowired MessageProcessorProperties properties) {
        statusListener = new PostamatStatusListener(properties, statusRefresher);
    }

    @Test
    void isValidMessage_ValidMessage_ReturnTrue() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getValidDto());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void isValidMessage_NullUuid_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullUUid());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void isValidMessage_NullOrderUuid_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullOrderUuid());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void isValidMessage_NullOperation_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullOperation());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void isValidMessage_NullDeliveryMode_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullDeliveryMode());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void isValidMessage_NullOperationTime_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullOperationTime());
        Boolean actual = statusListener.isValidMessage(message);
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getValidDto());
        Boolean isValid = statusListener.processValidation(message);
        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<PostomatEventDto> message = new GenericMessage<>(PostamatStatusTestUtils.getDtoWithNullUUid());
        Boolean isValid = statusListener.processValidation(message);
        Assertions.assertThat(isValid).isFalse();
    }
}
