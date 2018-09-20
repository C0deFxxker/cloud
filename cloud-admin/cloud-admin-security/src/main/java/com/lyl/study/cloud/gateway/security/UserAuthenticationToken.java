package com.lyl.study.cloud.gateway.security;

import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liyilin
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {
    private RoleDTO currentRole;

    public UserAuthenticationToken(UserDetailDTO user, RoleDTO currentRole) {
        super(resolveGrantedAuthorities(currentRole));
        Assert.notNull(user, "user cannot be null.");
        setDetails(user);
        this.currentRole = currentRole;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return ((UserDetailDTO) getDetails()).getPassword();
    }

    @Override
    public Object getPrincipal() {
        return ((UserDetailDTO) getDetails()).getUsername();
    }

    public RoleDTO getCurrentRole() {
        return currentRole;
    }

    private static List<SimpleGrantedAuthority> resolveGrantedAuthorities(RoleDTO roleDTO) {
        if (roleDTO != null) {
            return roleDTO.getPermissions().stream()
                    .filter(entity -> entity.getType() == 2 && !StringUtils.isEmpty(entity.getSign()))
                    .map(entity -> new SimpleGrantedAuthority(entity.getSign()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(0);
        }
    }
}
