package com.mjc.school.controller.exceptions;

import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.API_VERSION_NOT_SUPPORTED;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.RESOURCE_NOT_FOUND;
import static com.mjc.school.service.exceptions.ExceptionErrorCodes.VALIDATION_EXCEPTION;

@RestControllerAdvice
public class ApiExceptionHandler {

    public static final String RECORD_UNIQUENESS_RESTRICTION_MESSAGE = "A repeated field value violates the record uniqueness restriction";

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                VALIDATION_EXCEPTION.getErrorCode(),
                e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                RESOURCE_NOT_FOUND.getErrorCode(),
                e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(ApiVersionNotSupportedException.class)
    public ResponseEntity<Object> handleNotFoundException(ApiVersionNotSupportedException e) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ApiException apiException = new ApiException(
                API_VERSION_NOT_SUPPORTED.getErrorCode(),
                e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                VALIDATION_EXCEPTION.getErrorCode(),
                RECORD_UNIQUENESS_RESTRICTION_MESSAGE,
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return new ResponseEntity<>(apiException, status);
    }
}
