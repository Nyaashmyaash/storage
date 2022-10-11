package com.cdek.storage.infrastructure.exceptions;

import com.cdek.exceptions.ErrorInfo;
import com.cdek.exceptions.ExceptionInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Getter
@NoArgsConstructor
public class NotFoundEventException extends RuntimeException implements ExceptionInfo {

    private static final String DEFAULT_ERROR_CODE = "cant.find.event.by.order.id";

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * @return локаль.
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = true)
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    @Override
    public List<ErrorInfo> getErrors() {
        return Collections.singletonList(ErrorInfo.builder()
                .code(DEFAULT_ERROR_CODE)
                .build());
    }
}
