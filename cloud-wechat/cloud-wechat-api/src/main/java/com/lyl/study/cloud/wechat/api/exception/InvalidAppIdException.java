package com.lyl.study.cloud.wechat.api.exception;

import com.lyl.study.cloud.base.exception.BaseException;

import com.lyl.study.cloud.wechat.api.ErrorCode;
import org.springframework.boot.logging.LogLevel;

public class InvalidAppIdException extends BaseException {
    public InvalidAppIdException(String msg) {
        super(ErrorCode.INVALID_APPID, msg);
    }

    public InvalidAppIdException(String msg, LogLevel logLevel) {
        super(ErrorCode.INVALID_APPID, msg, logLevel);
    }

    public InvalidAppIdException(int code, String msg) {
        super(code, msg);
    }

    public InvalidAppIdException(int code, String msg, LogLevel logLevel) {
        super(code, msg, logLevel);
    }
}