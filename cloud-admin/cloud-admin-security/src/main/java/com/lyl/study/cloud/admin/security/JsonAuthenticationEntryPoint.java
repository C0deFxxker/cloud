package com.lyl.study.cloud.admin.security;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lyl.study.cloud.gateway.api.GatewayErrorCode.*;

/**
 * @author liyilin
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof InternalAuthenticationServiceException) {
            HttpServletUtils.writeJson(200,
                    new Result<>(INTERNAL_ERROR, exception.getMessage(), null),
                    response);
        } else if (exception instanceof UsernameNotFoundException) {
            HttpServletUtils.writeJson(200,
                    new Result<>(USERNAME_NOT_FOUND, exception.getMessage(), null),
                    response);
        } else if (exception instanceof BadCredentialsException) {
            HttpServletUtils.writeJson(200,
                    new Result<>(BAD_CREDENTIALS, exception.getMessage(), null),
                    response);
        } else if (exception instanceof DisabledException) {
            HttpServletUtils.writeJson(200,
                    new Result<>(ACCOUNT_DISABLED, exception.getMessage(), null),
                    response);
        } else if (exception instanceof InsufficientAuthenticationException) {
            HttpServletUtils.writeJson(200,
                    new Result<>(FORBIDDEN, exception.getMessage(), request.getRequestURI()),
                    response);
        } else {
            HttpServletUtils.writeJson(200,
                    new Result<>(INTERNAL_ERROR, exception.getMessage(), null),
                    response);
        }
    }
}

