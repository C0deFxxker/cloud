package com.lyl.study.cloud.base.controller;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MaintainErrorController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(MaintainErrorController.class);
    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    public String handleError(HttpServletRequest request) {
        int code = Integer.parseInt(request.getAttribute("javax.servlet.error.status_code").toString());
        String message = request.getAttribute("javax.servlet.error.message").toString();
        String data = request.getAttribute("javax.servlet.error.request_uri").toString();

        if (message == null || message.isEmpty()) {
            Object error = request.getAttribute("org.springframework.boot.autoconfigure.web.DefaultErrorAttributes.ERROR");
            if (error != null) {
                if (error instanceof MethodArgumentNotValidException) {
                    StringBuilder msgBuilder = new StringBuilder();
                    BindingResult bindingResult = ((MethodArgumentNotValidException) error).getBindingResult();
                    List<ObjectError> allErrors = bindingResult.getAllErrors();
                    for (ObjectError eachError : allErrors) {
                        Object o = eachError.getArguments()[0];
                        String msg = eachError.getDefaultMessage();
                        msgBuilder.append(o.toString()).append(msg).append(",");
                    }
                    if (msgBuilder.length() > 0) {
                        msgBuilder.deleteCharAt(msgBuilder.length() - 1);
                    }
                    message = msgBuilder.toString();
                } else if (error instanceof Throwable) {
                    message = ((Throwable) error).getMessage();
                } else {
                    message = error.toString();
                }
            }
        }

        if (code == 500) {
            Object attribute = request.getAttribute("javax.servlet.error.exception");
            if (attribute != null) {
                data = attribute.toString();
                Throwable throwable;
                while (attribute instanceof NestedServletException) {
                    attribute = ((NestedServletException) attribute).getCause();
                }
                throwable = (Throwable) attribute;
                message = throwable.getMessage();
            }
        }

        return JsonUtils.toJson(new Result<>(code, message, data));
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
