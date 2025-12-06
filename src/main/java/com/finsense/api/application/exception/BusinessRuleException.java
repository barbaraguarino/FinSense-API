package com.finsense.api.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessRuleException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String titleCode;
    private final String messageCode;
    private final Object[] args;

    public BusinessRuleException(HttpStatus httpStatus, String titleCode, String messageCode, Object... args) {
        super(messageCode);
        this.httpStatus = httpStatus;
        this.titleCode = titleCode;
        this.messageCode = messageCode;
        this.args = args;
    }
}

