package com.cdek.storage.infrastructure.controller.transport.validation;

import com.cdek.exceptions.builder.ValidateExceptionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class RequestParameterValidatorTest {

    @SpyBean
    ValidateExceptionBuilder builders;

    RequestParameterValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequestParameterValidator();
    }

    @Test
    void validate_ValidNumber_NoInteractions() {
        validator.validate("11111");
        Mockito.verifyNoInteractions(builders);
    }
}
