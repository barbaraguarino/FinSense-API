package com.finsense.api.application.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {

    private final String titleCode;
    private final String messageCode;
    private final Object[] args;

    public BusinessRuleException(String titleCode, String messageCode, Object... args) {
        super(messageCode);
        this.titleCode = titleCode;
        this.messageCode = messageCode;
        this.args = args;
    }
}

