package com.lyl.study.cloud.base.exception;

import com.lyl.study.cloud.base.CommonErrorCode;
import org.springframework.boot.logging.LogLevel;

public class NoSuchEntityException extends BaseException {
    public NoSuchEntityException(String msg) {
        super(CommonErrorCode.NOT_FOUND, msg);
    }

    public NoSuchEntityException(String msg, LogLevel logLevel) {
        super(CommonErrorCode.NOT_FOUND, msg, logLevel);
    }

    public NoSuchEntityException(int code, String msg) {
        super(code, msg);
    }

    public NoSuchEntityException(int code, String msg, LogLevel logLevel) {
        super(code, msg, logLevel);
    }
}
