package com.lyl.study.cloud.wxclient.web.vip.security;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResultLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(CommonErrorCode.OK, "注销成功", authentication.getPrincipal()),
                    response
            );
        } else {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(CommonErrorCode.UNAUTHORIZED, "尚未登录", null),
                    response
            );
        }
    }
}

