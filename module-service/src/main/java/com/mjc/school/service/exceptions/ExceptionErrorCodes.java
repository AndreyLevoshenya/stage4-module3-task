package com.mjc.school.service.exceptions;

//TODO при добавлении нарушение уникальности поля
public enum ExceptionErrorCodes {
    NEWS_DOES_NOT_EXIST("000001", "News with id %d does not exist"),
    AUTHOR_DOES_NOT_EXIST("000002", "Author id does not exist. Author id is: %s"),
    TAG_DOES_NOT_EXIST("000003", "Tag with id %d does not exist"),
    COMMENT_DOES_NOT_EXIST("000004", "Comment with id %d does not exist"),
    VALIDATION_EXCEPTION("000005", "Validation failed %s"),
    API_VERSION_NOT_SUPPORTED("000006", "Api version %s is not supported"),
    RESOURCE_NOT_FOUND("000007", "Resource not found"),
    USER_DOES_NOT_EXIST("000008", "User with username %s does not exist");

    private final String errorCode;
    private final String errorMessage;

    ExceptionErrorCodes(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
