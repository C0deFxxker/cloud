package com.lyl.study.cloud.base.security;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lyl.study.cloud.base.CommonErrorCode.FORBIDDEN;

/**
 * @author liyilin
 */
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 改成三元组形式
        HttpServletUtils.writeJson(HttpStatus.FORBIDDEN.value(),
                new Result<>(FORBIDDEN, accessDeniedException.getMessage(), null),
                response);
    }
}
