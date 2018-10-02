package com.lyl.study.cloud.base.security;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import org.springframework.http.HttpStatus;
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

import static com.lyl.study.cloud.base.CommonErrorCode.*;

/**
 * @author liyilin
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof InternalAuthenticationServiceException) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(INTERNAL_ERROR, exception.getMessage(), null),
                    response);
        } else if (exception instanceof UsernameNotFoundException) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(USERNAME_NOT_FOUND, exception.getMessage(), null),
                    response);
        } else if (exception instanceof BadCredentialsException) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(BAD_CREDENTIALS, exception.getMessage(), null),
                    response);
        } else if (exception instanceof DisabledException) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(ACCOUNT_DISABLED, exception.getMessage(), null),
                    response);
        } else if (exception instanceof InsufficientAuthenticationException) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(FORBIDDEN, exception.getMessage(), request.getRequestURI()),
                    response);
        } else {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(INTERNAL_ERROR, exception.getMessage(), null),
                    response);
        }
    }
}