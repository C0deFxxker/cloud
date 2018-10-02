package com.lyl.study.cloud.admin.security.exception;

import com.lyl.study.cloud.base.exception.BaseException;
import com.lyl.study.cloud.system.api.SystemErrorCode;
import org.springframework.boot.logging.LogLevel;

public class InvalidRoleException extends BaseException {
    public InvalidRoleException(String msg) {
        super(SystemErrorCode.INVALD_ROLE, msg);
    }

    public InvalidRoleException(String msg, LogLevel logLevel) {
        super(SystemErrorCode.INVALD_ROLE, msg, logLevel);
    }
}
