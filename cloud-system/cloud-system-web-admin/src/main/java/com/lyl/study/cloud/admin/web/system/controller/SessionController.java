package com.lyl.study.cloud.admin.web.system.controller;

import com.lyl.study.cloud.admin.security.CurrentSessionHolder;
import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;
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
        UserDetailDTO currentUser = CurrentSessionHolder.getCurrentUser();
        if (currentUser != null) {
            return new Result<>(CommonErrorCode.OK, "查询成功", currentUser);
        } else {
            return new Result<>(CommonErrorCode.FORBIDDEN, "尚未登录", null);
        }
    }
}
