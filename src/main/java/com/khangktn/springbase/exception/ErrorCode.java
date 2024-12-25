package com.khangktn.springbase.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error!"),
    INVALID_KEY(1001, "Invalid message key"),
    USER_EXIST(1002, "User is exist!"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters!"),
    PASSWORD_INVALID(1004, "Password must be at least 4 characters!"),
    USER_NOT_EXIST(1005, "User isn't exist!")
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
