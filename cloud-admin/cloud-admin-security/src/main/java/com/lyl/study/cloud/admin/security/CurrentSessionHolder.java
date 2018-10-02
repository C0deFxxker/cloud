package com.lyl.study.cloud.admin.security;

import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 更方便地获取当前会话信息
 *
 * @author liyilin
 */
public abstract class CurrentSessionHolder {
    public static UserDetailDTO getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getDetails();
        } else {
            return null;
        }
    }

    public static RoleDTO getCurrentRole() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return ((UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getCurrentRole();
        } else {
            return null;
        }
    }
}
