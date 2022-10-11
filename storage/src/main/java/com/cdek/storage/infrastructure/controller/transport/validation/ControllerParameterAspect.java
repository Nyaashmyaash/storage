package com.cdek.storage.infrastructure.controller.transport.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ControllerParameterAspect {

    private static final String NOT_FOUND_PARAM = "Required param don't found";

    private final RequestParameterValidator validator;

    @Pointcut(
            "execution(@com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect" +
                    ".Applied public * getOrderStoragePeriodEventByOrderNumber(." +
                    ".))")
    public void publicAnnotatedMethodGetOrderStoragePeriodEventByOrderNumber() { //@Pointcut
    }

    @Pointcut(
            "execution(@com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect" +
                    ".Applied public * getOrderStoragePeriodEventByOrderUuid(." +
                    ".))")
    public void publicAnnotatedMethodGetOrderStoragePeriodEventByOrderUuid() { //@Pointcut
    }

    @Pointcut(
            "execution(@com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect" +
                    ".Applied public * sendOrderStoragePeriodEventByOrderNumber(." +
                    ".))")
    public void publicAnnotatedMethodSendOrderStoragePeriodEventByOrderNumber() { //@Pointcut
    }

    @Pointcut(
            "execution(@com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect" +
                    ".Applied public * sendOrderStoragePeriodEventByOrderUuid(." +
                    ".))")
    public void publicAnnotatedMethodSendOrderStoragePeriodEventByOrderUuid() { //@Pointcut
    }

    @Pointcut(
            "execution(@com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect" +
                    ".Applied public * fixOrderStoragePeriod(." +
                    ".))")
    public void publicAnnotatedMethodFixOrderStoragePeriod() { //@Pointcut
    }

    @Before("publicAnnotatedMethodGetOrderStoragePeriodEventByOrderNumber()")
    public void validateGetOrderStoragePeriodEventByOrderNumberRequest(JoinPoint point) {
        Optional<String> request = Arrays.stream(point.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst();
        if (request.isPresent()) {
            validator.validate(request.get());
        } else {
            log.warn(NOT_FOUND_PARAM);
        }
    }

    @Before("publicAnnotatedMethodGetOrderStoragePeriodEventByOrderUuid()")
    public void validateGetOrderStoragePeriodEventByOrderUuidRequest(JoinPoint point) {
        Optional<String> request = Arrays.stream(point.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst();
        if (request.isPresent()) {
            validator.validateUuid(request.get());
        } else {
            log.warn(NOT_FOUND_PARAM);
        }
    }

    @Before("publicAnnotatedMethodSendOrderStoragePeriodEventByOrderNumber()")
    public void validateSendOrderStoragePeriodEventByOrderNumberRequest(JoinPoint point) {
        Optional<String> request = Arrays.stream(point.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst();
        if (request.isPresent()) {
            validator.validate(request.get());
        } else {
            log.warn(NOT_FOUND_PARAM);
        }
    }

    @Before("publicAnnotatedMethodSendOrderStoragePeriodEventByOrderUuid()")
    public void validateSendOrderStoragePeriodEventByOrderUuidRequest(JoinPoint point) {
        Optional<String> request = Arrays.stream(point.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst();
        if (request.isPresent()) {
            validator.validateUuid(request.get());
        } else {
            log.warn(NOT_FOUND_PARAM);
        }
    }

    @Before("publicAnnotatedMethodFixOrderStoragePeriod()")
    public void validateFixOrderStoragePeriodRequest(JoinPoint point) {
        List<String> request = Arrays.stream(point.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toList());
        if (!request.isEmpty()) {
            validator.validateDate(request);
        } else {
            log.warn(NOT_FOUND_PARAM);
        }
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Applied {
    }
}
