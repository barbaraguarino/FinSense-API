package com.finsense.api.presentation.advice;

import com.finsense.api.application.exception.BusinessRuleException;
import com.finsense.api.presentation.dto.ErrorDTO;
import com.finsense.api.presentation.dto.ErrorDetailDTO;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleBusinessRuleException(
            BusinessRuleException exception,
            HttpServletRequest request
    ) {
        ErrorDTO dto = createErrorDTO(
                HttpStatus.UNPROCESSABLE_CONTENT,
                exception.getTitleCode(),
                exception.getMessageCode(),
                exception.getArgs(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(dto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ErrorDetailDTO> details = mapBindingResultToDetails(
                exception.getBindingResult(),
                LocaleContextHolder.getLocale()
        );

        ErrorDTO dto = createErrorDTO(
                HttpStatus.BAD_REQUEST,
                "error.validation.title",
                "error.validation.message",
                null,
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorDTO> handleGenericException(
            HttpServletRequest request
    ) {
        ErrorDTO dto = createErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "error.server.title",
                "error.server.generic",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

    private List<ErrorDetailDTO> mapBindingResultToDetails(
            BindingResult bindingResult,
            Locale locale) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String message = messageSource.getMessage(fieldError, locale);
                    return new ErrorDetailDTO(fieldError.getField(), message);
                })
                .toList();
    }

    private ErrorDTO createErrorDTO(
            HttpStatus status,
            String titleCode,
            String messageCode,
            @Nullable Object[] args,
            String path,
            List<ErrorDetailDTO> details) {

        Locale locale = LocaleContextHolder.getLocale();

        String title = messageSource.getMessage(
                titleCode,
                new Object[]{},
                titleCode,
                locale);

        String message = messageSource.getMessage(
                messageCode,
                args != null ? args : new Object[]{},
                messageCode,
                locale);

        return new ErrorDTO(
                LocalDateTime.now(),
                status.value(),
                title,
                message,
                path,
                details
        );
    }
}
