package com.cdek.storage.infrastructure.controller.exceptionhandler;

import com.cdek.exceptions.web.controller.DefaultControllerExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends DefaultControllerExceptionHandler {

    // MessageSource нужен для локализации сообщений исключений из пакета com.cdek.exceptions.
    public ControllerExceptionHandler(MessageSource messageSource) {
        super(messageSource);
    }
}
