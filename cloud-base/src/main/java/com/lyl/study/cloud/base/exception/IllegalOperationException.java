package com.lyl.study.cloud.base.exception;

import org.springframework.boot.logging.LogLevel;

public class IllegalOperationException extends BaseException {
    public IllegalOperationException(int code, String msg) {
        super(code, msg);
    }

    public IllegalOperationException(int code, String msg, LogLevel logLevel) {
        super(code, msg, logLevel);
    }
}
