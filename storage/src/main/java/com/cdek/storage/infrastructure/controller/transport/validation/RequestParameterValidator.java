package com.cdek.storage.infrastructure.controller.transport.validation;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestParameterValidator {

    public static final String PARAMETER_ORDER_NUMBER = "orderNumber";
    public static final String PARAMETER_ORDER_UUID = "orderUuid";
    public static final String PARAMETER_DATE_FROM = "dateFrom";
    public static final String PARAMETER_DATE_TO = "dateTo";

    public void validate(String number) {
        ValidationUtils.isFieldExists(
                number,
                PARAMETER_ORDER_NUMBER
        );
    }

    public void validateUuid(String uuid) {
        ValidationUtils.isFieldExistsAndHasTypeUuid(
                uuid,
                PARAMETER_ORDER_UUID
        );
    }

    public void validateDate(List<String> period) {
        String dateFrom = period.get(0);
        String dateTo = period.get(1);

        ValidationUtils.isFieldExistsAndHasTypeInstant(
                dateFrom,
                PARAMETER_DATE_FROM
        );

        ValidationUtils.isFieldExistsAndHasTypeInstant(
                dateTo,
                PARAMETER_DATE_TO
        );

        ValidationUtils.dateToIsAfterDateFrom(dateFrom, dateTo);
    }
}
