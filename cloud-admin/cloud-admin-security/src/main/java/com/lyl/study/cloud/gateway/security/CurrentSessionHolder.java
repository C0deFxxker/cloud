package com.lyl.study.cloud.gateway.security;

import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 更方便地获取当前会话信息
 *
 * @author liyilin
 */
public abstract class CurrentSessionHolder {
    public static UserDetailDTO getCurrentUser() {
        return (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static RoleDTO getCurrentRole() {
        return ((UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getCurrentRole();
    }
}
