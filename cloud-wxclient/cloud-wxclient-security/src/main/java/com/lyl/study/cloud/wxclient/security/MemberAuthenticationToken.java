package com.lyl.study.cloud.wxclient.security;

import com.lyl.study.cloud.member.api.dto.response.MemberDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * @author liyilin
 */
public class MemberAuthenticationToken extends AbstractAuthenticationToken {
    public MemberAuthenticationToken(MemberDTO member) {
        super(null);
        Assert.notNull(member, "member cannot be null.");
        setDetails(member);
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return ((MemberDTO) getDetails()).getUid();
    }
}
