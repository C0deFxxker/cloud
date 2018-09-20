package com.lyl.study.cloud.wechat.api.exception;

import com.lyl.study.cloud.base.exception.BaseException;
import com.lyl.study.cloud.wechat.api.ErrorCode;
import org.springframework.boot.logging.LogLevel;

public class WxApiRemoteException extends BaseException {
    public WxApiRemoteException(String msg) {
        super(ErrorCode.WX_ERROR, msg);
    }

    public WxApiRemoteException(String msg, LogLevel logLevel) {
        super(ErrorCode.WX_ERROR, msg, logLevel);
    }

    public WxApiRemoteException(int code, String msg) {
        super(code, msg);
    }

    public WxApiRemoteException(int code, String msg, LogLevel logLevel) {
        super(code, msg, logLevel);
    }
}
