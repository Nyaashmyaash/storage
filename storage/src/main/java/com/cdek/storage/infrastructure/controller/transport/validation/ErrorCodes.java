package com.cdek.storage.infrastructure.controller.transport.validation;

import com.cdek.exceptions.ErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodes implements ErrorMessage {
    REQUIRED_FIELD("required.field"),
    WRONG_INPUT_PARAM_TYPE("wrong.input.param.type"),
    ERROR_DATES_NOT_VALID("error.dates.not.valid");

    private final String code;
}
