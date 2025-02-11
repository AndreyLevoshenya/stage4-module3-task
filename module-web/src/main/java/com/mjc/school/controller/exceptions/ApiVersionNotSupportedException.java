package com.mjc.school.controller.exceptions;

public class ApiVersionNotSupportedException extends RuntimeException {
    public ApiVersionNotSupportedException(String message) {
        super(message);
    }
}
