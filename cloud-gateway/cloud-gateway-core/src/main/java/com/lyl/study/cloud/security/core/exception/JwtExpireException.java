package com.lyl.study.cloud.security.core.exception;

public class JwtExpireException extends InvalidJwtException {
    public JwtExpireException() {
    }

    public JwtExpireException(String message) {
        super(message);
    }

    public JwtExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtExpireException(Throwable cause) {
        super(cause);
    }

    public JwtExpireException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
