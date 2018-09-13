package com.lyl.study.cloud.base.exception;

import org.springframework.boot.logging.LogLevel;

public class InvalidArgumentException extends BaseException {
    public InvalidArgumentException(int code, String msg) {
        super(code, msg);
    }

    public InvalidArgumentException(int code, String msg, LogLevel logLevel) {
        super(code, msg, logLevel);
    }
}
