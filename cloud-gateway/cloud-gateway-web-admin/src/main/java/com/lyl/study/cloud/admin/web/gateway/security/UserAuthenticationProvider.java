package com.lyl.study.cloud.admin.web.gateway.security;

import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.gateway.security.UserAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class UserAuthenticationProvider implements AuthenticationProvider {
    private UserFacade userFacade;

    public UserAuthenticationProvider(UserFacade userFacade) {
        this.userFacade = userFacade;

        Assert.notNull(userFacade, "userFacade cannot be null");
    }

    /**
     * 校验用户信息
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAuthenticationToken token = (UserAuthenticationToken) authentication;

        if (isLegalArguments(token)) {
            throw new IllegalArgumentException("请传入 username, password 进行校验");
        }

        UserDetailDTO user = userFacade.getByUsername((String) token.getPrincipal());

        if (user == null) {
            throw new UsernameNotFoundException("找不到账号");
        }

        if (!user.getPassword().equals(token.getCredentials())) {
            throw new BadCredentialsException("密码不正确");
        }

        if (!user.getEnable()) {
            throw new DisabledException("用户已被冻结");
        }

        ((UserAuthenticationToken) authentication).setDetails(user);
        authentication.setAuthenticated(true);

        return authentication;
    }

    private boolean isLegalArguments(UserAuthenticationToken token) {
        return (token == null
                || StringUtils.isEmpty(token.getPrincipal())
                || StringUtils.isEmpty(token.getCredentials()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
