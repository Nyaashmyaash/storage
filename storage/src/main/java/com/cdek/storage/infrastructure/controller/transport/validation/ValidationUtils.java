package com.cdek.storage.infrastructure.controller.transport.validation;

import com.cdek.exceptions.ConstraintViolation;
import com.cdek.exceptions.builder.ExceptionBuilders;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class ValidationUtils {

    public static void isFieldExists(String field, String fieldName) {
        if (field == null || field.isEmpty() || field.isBlank()) {
            throw ExceptionBuilders.validate()
                    .violation(ConstraintViolation.builder()
                            .code(ErrorCodes.REQUIRED_FIELD)
                            .argument(fieldName)
                            .build())
                    .build();
        }
    }

    public static void isFieldExistsAndHasTypeUuid(String field, String fieldName) {
        isFieldExists(field, fieldName);
        isFieldHasTypeUuid(field, fieldName);
    }

    public static void isFieldHasTypeUuid(String field, String fieldName) {
        try {
            UUID.fromString(field);
        } catch (Exception e) {
            throw ExceptionBuilders.validate()
                    .cause(e)
                    .violation(ConstraintViolation.builder()
                            .code(ErrorCodes.WRONG_INPUT_PARAM_TYPE)
                            .argument(fieldName)
                            .build())
                    .build();
        }
    }

    public static void isFieldExistsAndHasTypeInstant(String field, String fieldName) {
        isFieldExists(field, fieldName);
        isFieldHasTypeInstant(field, fieldName);
    }

    public static void isFieldHasTypeInstant(String field, String fieldName) {
        try {
            Instant.parse(field);
        } catch (Exception e) {
            throw ExceptionBuilders.validate()
                    .cause(e)
                    .violation(ConstraintViolation.builder()
                            .code(ErrorCodes.WRONG_INPUT_PARAM_TYPE)
                            .argument(fieldName)
                            .build())
                    .build();
        }
    }

    public static void dateToIsAfterDateFrom(String dateFrom, String dateTo) {
        if (Instant.parse(dateFrom).isAfter(Instant.parse(dateTo))) {
            throw ExceptionBuilders.validate()
                    .violation(ConstraintViolation.builder()
                            .code(ErrorCodes.ERROR_DATES_NOT_VALID)
                            .build())
                    .build();
        }
    }
}
