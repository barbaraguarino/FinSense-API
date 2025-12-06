package com.finsense.api.presentation.advice;

import com.finsense.api.application.exception.BusinessRuleException;
import com.finsense.api.application.user.exception.EmailDuplicateException;
import com.finsense.api.presentation.dto.ErrorDTO;
import com.finsense.api.presentation.dto.ErrorDetailDTO;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleBusinessRuleException(
            BusinessRuleException exception,
            HttpServletRequest request
    ) {
        ErrorDTO dto = createErrorDTO(
                exception.getHttpStatus(),
                exception.getTitleCode(),
                exception.getMessageCode(),
                exception.getArgs(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(exception.getHttpStatus()).body(dto);
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
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Erro interno não tratado capturado no GlobalExceptionHandler:", exception);

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleHttpMessageNotReadableException(
            HttpServletRequest request
    ) {

        ErrorDTO dto = createErrorDTO(
                HttpStatus.BAD_REQUEST,
                "error.json.title",
                "error.json.message",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        Object[] args = {exception.getName(), exception.getValue()};

        ErrorDTO dto = createErrorDTO(
                HttpStatus.BAD_REQUEST,
                "error.type_mismatch.title",
                "error.type_mismatch.message",
                args,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        Object[] args = {exception.getMethod()};

        ErrorDTO dto = createErrorDTO(
                HttpStatus.METHOD_NOT_ALLOWED,
                "error.method_not_allowed.title",
                "error.method_not_allowed.message",
                args,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(dto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ErrorDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.error("Violação de integridade de dados: {}", exception.getMessage());

        ErrorDTO dto = createErrorDTO(
                HttpStatus.CONFLICT,
                "error.conflict.title",
                "error.conflict.message",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

    /*

    Dependência do Spring Security ainda não implementada ou configurada.

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(
            HttpServletRequest request
    ) {
        ErrorDTO dto = createErrorDTO(
                HttpStatus.FORBIDDEN,
                "error.access_denied.title",
                "error.access_denied.message",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
    }

    */

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
