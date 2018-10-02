package com.lyl.study.cloud.wxclient.security;

import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 更方便地获取当前会话信息
 *
 * @author liyilin
 */
public abstract class CurrentSessionHolder {
    public static MemberDTO getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return (MemberDTO) SecurityContextHolder.getContext().getAuthentication().getDetails();
        } else {
            return null;
        }
    }
}
