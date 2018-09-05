package com.lyl.study.cloud.base.exception;

import com.lyl.study.cloud.base.CommonErrorCode;

public class DataNotFoundException extends BaseException {
    public DataNotFoundException(String msg) {
        super(CommonErrorCode.NOT_FOUND, msg);
    }
}
