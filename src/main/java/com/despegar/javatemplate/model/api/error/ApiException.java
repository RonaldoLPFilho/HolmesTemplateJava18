package com.despegar.javatemplate.model.api.error;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public abstract class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3156907166876945712L;

    private final HttpStatus httpStatus;
    private final ErrorResponse errorResponse;

    public ApiException(String message, HttpStatus httpStatus, ErrorResponse errorResponse) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
