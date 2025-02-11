package com.mjc.school.controller.exceptions;

import com.mjc.school.service.exceptions.NotFoundException;
import com.mjc.school.service.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.mjc.school.service.exceptions.ExceptionErrorCodes.*;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<Object> handleValidationException(ValidationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                VALIDATION_EXCEPTION.getErrorCode(), e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                RESOURCE_NOT_FOUND.getErrorCode(), e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = {ApiVersionNotSupportedException.class})
    public ResponseEntity<Object> handleNotFoundException(ApiVersionNotSupportedException e) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ApiException apiException = new ApiException(
                API_VERSION_NOT_SUPPORTED.getErrorCode(), e.getMessage(),
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        return new ResponseEntity<>(apiException, status);
    }
}
