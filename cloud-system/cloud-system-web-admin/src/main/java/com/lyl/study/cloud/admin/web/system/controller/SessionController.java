package com.lyl.study.cloud.admin.web.system.controller;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.SystemErrorCode;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.admin.security.CurrentSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    @Autowired

    @GetMapping("/currentUser")
    public Result<UserDetailDTO> getCurrentSession() {
        return new Result<>(SystemErrorCode.OK, "查询成功", CurrentSessionHolder.getCurrentUser());
    }
}
