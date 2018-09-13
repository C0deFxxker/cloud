package com.lyl.study.cloud.base.exception;

import com.lyl.study.cloud.base.dto.Result;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class BaseException extends RuntimeException {
    private static final String MSG_FIELD = "detailMessage";

    private int code;
    private LogLevel logLevel = LogLevel.ERROR;

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BaseException(int code, String msg, LogLevel logLevel) {
        super(msg);
        this.code = code;
        this.logLevel = logLevel;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return this.getMessage();
    }

    public void setMsg(String msg) {
        Field field = ReflectionUtils.findField(this.getClass(), MSG_FIELD, String.class);
        field.setAccessible(true);
        ReflectionUtils.setField(field, this, msg);
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public <T> Result<T> toResult() {
        return new Result<>(code, getMsg(), null);
    }
}
