package com.lyl.study.cloud.gateway.security;

import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liyilin
 */
public class UserAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private RoleDTO currentRole;

    public UserAuthenticationToken(UserDetailDTO user, RoleDTO currentRole) {
        super(user.getUsername(), user.getPassword(), resolveGrantedAuthorities(currentRole));
        setDetails(user);
        this.currentRole = currentRole;
    }

    public RoleDTO getCurrentRole() {
        return currentRole;
    }

    private static List<SimpleGrantedAuthority> resolveGrantedAuthorities(RoleDTO roleDTO) {
        return roleDTO.getPermissions().stream().
                map(entity -> new SimpleGrantedAuthority(entity.getSign()))
                .collect(Collectors.toList());
    }
}
