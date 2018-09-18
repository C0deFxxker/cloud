package com.lyl.study.cloud.admin.web.gateway.controller;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.GatewayErrorCode;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    @GetMapping("/currentUser")
    public Result<UserDetailDTO> getCurrentSession() {
        return new Result<>(GatewayErrorCode.OK, "查询成功", CurrentSessionHolder.getCurrentUser());
    }
}
