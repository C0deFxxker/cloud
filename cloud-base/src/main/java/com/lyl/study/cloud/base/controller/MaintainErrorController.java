package com.lyl.study.cloud.base.controller;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.handler.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class MaintainErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @Autowired
    private CommonExceptionHandler exceptionHandler;

    @RequestMapping(value = ERROR_PATH)
    public Result<?> handleError(HttpServletRequest request) {
        int code = Integer.parseInt(request.getAttribute("javax.servlet.error.status_code").toString());

        Object error = request.getAttribute("org.springframework.boot.autoconfigure.web.DefaultErrorAttributes.ERROR");
        if (error instanceof Throwable) {
            return exceptionHandler.handle((Throwable) error);
        }

        error = request.getAttribute("javax.servlet.error.exception");
        if (error instanceof Throwable) {
            return exceptionHandler.handle((Throwable) error);
        }

        String message = request.getAttribute("javax.servlet.error.message").toString();
        String data = request.getAttribute("javax.servlet.error.request_uri").toString();

        Result<String> result = new Result<>(code, message, data);
        if (log.isDebugEnabled()) {
            log.debug("错误处理结果：" + result.toString());
        }

        return result;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
