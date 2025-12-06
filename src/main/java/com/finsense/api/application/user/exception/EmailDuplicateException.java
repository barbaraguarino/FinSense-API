package com.finsense.api.application.user.exception;

import com.finsense.api.application.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;

public class EmailDuplicateException extends BusinessRuleException{

    public EmailDuplicateException(
            String titleCode,
            String messageCode,
            Object... args
    ){
        super(HttpStatus.CONFLICT, titleCode, messageCode, args);
    }
}
