package com.lyl.study.cloud.wxclient.web.vip.security;

import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import com.lyl.study.cloud.wxclient.security.MemberAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class WxClientAuthenticationProvider implements AuthenticationProvider {
    private MemberFacade memberFacade;

    public WxClientAuthenticationProvider(MemberFacade memberFacade) {
        this.memberFacade = memberFacade;

        Assert.notNull(memberFacade, "memberFacade cannot be null");
    }

    /**
     * 校验用户信息
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MemberAuthenticationToken token = (MemberAuthenticationToken) authentication;

        if (isLegalArguments(token)) {
            throw new IllegalArgumentException("请传入 mobile 进行校验");
        }

        MemberDTO member = memberFacade.getByMobile(token.getMobile());

        if (member == null) {
            throw new UsernameNotFoundException("找不到手机号");
        }

        if (!member.getEnable()) {
            throw new DisabledException("会员已被冻结");
        }

        ((MemberAuthenticationToken) authentication).setDetails(member);
        authentication.setAuthenticated(true);

        return authentication;
    }

    private boolean isLegalArguments(MemberAuthenticationToken token) {
        return (token == null
                || StringUtils.isEmpty(((MemberDTO) token.getDetails()).getMobile()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MemberAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
