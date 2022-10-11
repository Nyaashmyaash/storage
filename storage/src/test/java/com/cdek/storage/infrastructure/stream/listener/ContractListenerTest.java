package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.contract.esb.client.ContractEventDto;
import com.cdek.scaling.autoconfigure.locks.LocalScalableLocksAutoConfiguration;
import com.cdek.scaling.autoconfigure.locks.ScalableLocksAutoConfiguration;
import com.cdek.storage.buffer.ports.input.ContractRefresher;
import com.cdek.storage.buffer.ports.output.ContractBufferRepository;
import com.cdek.storage.utils.ContractTestUtils;
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
class ContractListenerTest {

    @MockBean
    ContractRefresher contractRefresher;
    @MockBean
    ContractBufferRepository repository;

    ContractListener listener;

    @BeforeEach
    void setUp(@Autowired MessageProcessorProperties properties) {
        listener = new ContractListener(properties, repository, contractRefresher);
    }

    @Test
    void isValidMessage_ValidContractModel_ReturnTrue() {
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoTypeIM());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void isValidMessage_NotValidContractModelWithoutUuid_ReturnFalse() {
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoWithoutUuid());
        Boolean isValid = listener.isValidMessage(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void getCurrent_NotNullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString()))
                .thenReturn(ContractTestUtils.TIMESTAMP);
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoTypeIM());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNotNull().isEqualTo(ContractTestUtils.TIMESTAMP);
    }

    @Test
    void getCurrent_NullTimestampFromDb_Success() {
        Mockito.when(repository.getTimestamp(Mockito.anyString())).thenReturn(null);
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoTypeIM());
        Instant current = listener.getCurrent(message);

        Assertions.assertThat(current).isNull();
    }

    @Test
    void processValidation_ValidMessage_ReturnTrue() {
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoTypeIM());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void processValidation_NotValidMessage_ReturnFalse() {
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoWithoutUuid());
        Boolean isValid = listener.processValidation(message);

        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void processMessage_Message() {
        Message<ContractEventDto> message = new GenericMessage<>(ContractTestUtils.createContractEventDtoTypeIM());
        listener.processMessage(message, Instant.now());

        Mockito.verify(contractRefresher, Mockito.times(1)).refreshIfNeeded(Mockito.any(), Mockito.any());
    }
}
