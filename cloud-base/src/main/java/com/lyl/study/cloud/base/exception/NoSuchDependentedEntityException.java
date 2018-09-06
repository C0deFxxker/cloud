package com.lyl.study.cloud.base.exception;

public class NoSuchDependentedEntityException extends NoSuchEntityException {
    public NoSuchDependentedEntityException() {
    }

    public NoSuchDependentedEntityException(String message) {
        super(message);
    }

    public NoSuchDependentedEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDependentedEntityException(Throwable cause) {
        super(cause);
    }

    public NoSuchDependentedEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
