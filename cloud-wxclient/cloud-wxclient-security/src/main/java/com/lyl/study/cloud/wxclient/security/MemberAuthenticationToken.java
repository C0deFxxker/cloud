package com.lyl.study.cloud.wxclient.security;

import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * @author liyilin
 */
public class MemberAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 用于微信登录获取OpenID，非登录情况下没有任何作用
     */
    private String code;

    public MemberAuthenticationToken(MemberDTO member) {
        super(null);
        Assert.notNull(member, "member cannot be null.");
        setDetails(member);
        setAuthenticated(false);
    }

    public String getMobile() {
        return ((MemberDTO) getDetails()).getMobile();
    }

    public String getEmail() {
        return ((MemberDTO) getDetails()).getEmail();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return ((MemberDTO) getDetails()).getUid();
    }

    public String getCode() {
        return code;
    }

    public MemberAuthenticationToken setCode(String code) {
        this.code = code;
        return this;
    }
}
