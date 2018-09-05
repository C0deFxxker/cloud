package com.lyl.study.cloud.base.exception;

import com.lyl.study.cloud.base.dto.Result;

public abstract class BaseException extends RuntimeException {
    private int code;
    private String msg;

    public BaseException(int code, String msg) {
        super("[" + code + "]" + msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    protected void setMsg(String msg) {
        this.msg = msg;
    }

    public <T> Result<T> toResult() {
        return new Result<>(code, msg, null);
    }
}
