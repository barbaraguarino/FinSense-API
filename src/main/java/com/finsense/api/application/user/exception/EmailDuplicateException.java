package com.finsense.api.application.user.exception;

import com.finsense.api.application.exception.BusinessRuleException;

public class EmailDuplicateException extends BusinessRuleException {

    public EmailDuplicateException(String titleCode, String messageCode, Object... args) {
        super(titleCode, messageCode, args);
    }
}
