package com.finsense.api.presentation.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/welcome")
public class WelcomeController {

    private final MessageSource messageSource;

    public WelcomeController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<String> welcome() {
        Locale locale = LocaleContextHolder.getLocale();

        String message = Objects.requireNonNull(
                messageSource.getMessage("welcome.message", new Object[]{}, locale)
        );

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
