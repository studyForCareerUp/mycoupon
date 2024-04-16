package com.example.mycouponcore.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CouponIssueException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode, message);
    }
}
