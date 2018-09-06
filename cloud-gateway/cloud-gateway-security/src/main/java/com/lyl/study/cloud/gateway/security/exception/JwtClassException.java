package com.lyl.study.cloud.gateway.security.exception;

public class JwtClassException extends RuntimeException {
    public JwtClassException() {
    }

    public JwtClassException(String message) {
        super(message);
    }

    public JwtClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtClassException(Throwable cause) {
        super(cause);
    }

    public JwtClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
