package com.mjc.school.controller.exception;

public class ApiVersionNotSupportedException extends RuntimeException {
    public ApiVersionNotSupportedException(String message) {
        super(message);
    }
}
