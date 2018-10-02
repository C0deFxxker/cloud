package com.lyl.study.cloud.wxclient.security;

import com.lyl.study.cloud.base.exception.InvalidJwtException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.security.jwt.JwtConcurrentSessionFilter;
import com.lyl.study.cloud.base.security.jwt.JwtSigner;
import com.lyl.study.cloud.member.api.dto.response.MemberDTO;
import com.lyl.study.cloud.member.api.facade.MemberFacade;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import javax.servlet.ServletException;

/**
 * 基于JWT的会话用户识别过滤器
 *
 * @author liyilin
 */
@Slf4j
public class MemberJwtConcurrentSessionFilter extends JwtConcurrentSessionFilter {
    // 用户管理服务
    private MemberFacade memberFacade;

    public MemberJwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        super(jwtSigner);
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(memberFacade, "memberFacade cannot be null");
        Assert.notNull(jwtSigner, "jwtSigner cannot be null");
        Assert.hasText(cookieName, "cookieName cannot be blank text or null");

        if (cookiePath != null && !cookiePath.startsWith("/")) {
            cookiePath = "/" + cookiePath;
        }

        log.info("初始化完毕: CookieName={}, CookiePath={}, SessionAge={}",
                cookieName, cookiePath, jwtSigner.getExpire() / 1000);
    }

    @Override
    protected Object deserializeToken(String token) throws InvalidJwtException, ExpiredJwtException {
        return jwtSigner.deserializeToken(token, JwtClaims.class);
    }

    @Override
    protected void assertJwtClaimsValid(Object claims) throws InvalidJwtException {
        JwtClaims jwtClaims = (JwtClaims) claims;
        boolean invalid = (jwtClaims.getMemberId() == null
                || jwtClaims.getOpenId() == null
                || jwtClaims.getLoginTime() == null);
        if (invalid) {
            throw new InvalidJwtException("Jwt Claims invalid, [memberId, openId, loginTime]");
        }
    }

    @Override
    protected Authentication resolveAuthentication(Object claims) {
        JwtClaims jwtClaims = (JwtClaims) claims;
        Long memberId = jwtClaims.getMemberId();
        MemberDTO member = memberFacade.getById(memberId);
        if (member == null) {
            throw new NoSuchEntityException("找不到当前会员信息");
        }
        return new MemberAuthenticationToken(member);
    }

    public MemberFacade getMemberFacade() {
        return memberFacade;
    }

    public MemberJwtConcurrentSessionFilter setMemberFacade(MemberFacade memberFacade) {
        this.memberFacade = memberFacade;
        return this;
    }
}
