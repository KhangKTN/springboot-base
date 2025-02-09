package com.khangktn.springbase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXIST(1002, "User is exist!", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be in range {min} - {max} characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1005, "User isn't exist!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "User unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    DOB_INVALID(1008, "Age must be least {min}", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
