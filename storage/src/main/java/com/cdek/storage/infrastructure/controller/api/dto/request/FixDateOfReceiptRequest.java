package com.cdek.storage.infrastructure.controller.api.dto.request;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FixDateOfReceiptRequest {
    List<String> orderNumbers;
    String tariffMode;
    String dateFrom;
    String dateTo;
}
