package com.lyl.study.cloud.admin.web.system.security;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResultLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            HttpServletUtils.writeJson(200,
                    new Result<>(CommonErrorCode.OK, "注销成功", authentication.getPrincipal()),
                    response
            );
        } else {
            HttpServletUtils.writeJson(200,
                    new Result<>(CommonErrorCode.UNAUTHORIZED, "尚未登录", null),
                    response
            );
        }
    }
}

