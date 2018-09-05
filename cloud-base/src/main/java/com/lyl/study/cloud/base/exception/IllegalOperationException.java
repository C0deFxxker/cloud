package com.lyl.study.cloud.base.exception;

import com.lyl.study.cloud.base.CommonErrorCode;

public class IllegalOperationException extends BaseException {
    public IllegalOperationException(String msg) {
        super(CommonErrorCode.BAD_REQUEST, msg);
    }
}
