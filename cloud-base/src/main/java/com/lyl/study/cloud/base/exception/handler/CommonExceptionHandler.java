package com.lyl.study.cloud.base.exception.handler;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.NestedServletException;

import java.util.List;

import static com.lyl.study.cloud.base.CommonErrorCode.BAD_REQUEST;
import static com.lyl.study.cloud.base.CommonErrorCode.INTERNAL_ERROR;

/**
 * 统一异常处理器
 *
 * @author liyilin
 */
@ControllerAdvice
public class CommonExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Result<?> handle(Throwable e) {
        while (e instanceof NestedServletException) {
            e = e.getCause();
        }

        if (e instanceof BaseException) {
            return resolveBaseException((BaseException) e);
        } else {
            return resolveCommonException(e);
        }
    }

    protected Result<?> resolveCommonException(Throwable e) {
        if (e instanceof MethodArgumentNotValidException) {
            StringBuilder msgBuilder = new StringBuilder();
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError eachError : allErrors) {
                if (eachError instanceof FieldError) {
                    String field = ((FieldError) eachError).getField();
                    String msg = eachError.getDefaultMessage();
                    msgBuilder.append(field).append(msg).append(",");
                } else {
                    Object o = eachError.getArguments()[0];
                    String msg = eachError.getDefaultMessage();
                    msgBuilder.append(o.toString()).append(msg).append(",");
                }
            }
            if (msgBuilder.length() > 0) {
                msgBuilder.deleteCharAt(msgBuilder.length() - 1);
            }

            Result result = new Result<>(BAD_REQUEST, msgBuilder.toString(), null);

            if (logger.isDebugEnabled()) {
                logger.debug(result.toString());
            }

            return result;
        } else if (e instanceof IllegalArgumentException) {
            e.printStackTrace();
            return new Result<>(BAD_REQUEST, e.getMessage(), null);
        } else {
            e.printStackTrace();
            return new Result<>(INTERNAL_ERROR, e.getMessage(), null);
        }
    }

    protected Result<?> resolveBaseException(BaseException e) {
        if (LogLevel.ERROR.equals(e.getLogLevel())) {
            logger.error(e.toString());

            if (logger.isDebugEnabled()) {
                logger.debug("全局异常处理器捕捉到错误异常栈如下", e);
            }
        } else if (LogLevel.WARN.equals(e.getLogLevel())) {
            logger.warn(e.toString());
        } else if (LogLevel.INFO.equals(e.getLogLevel())) {
            logger.info(e.toString());
        } else if (LogLevel.TRACE.equals(e.getLogLevel())) {
            logger.trace(e.toString());
        } else if (LogLevel.DEBUG.equals(e.getLogLevel()) && logger.isDebugEnabled()) {
            logger.debug(e.toString());
        }
        return e.toResult();
    }
}
