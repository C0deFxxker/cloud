package com.lyl.study.cloud.wxclient.web.vip.security;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.security.jwt.JwtSigner;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import com.lyl.study.cloud.base.util.JsonUtils;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.wechat.api.facade.WxOAuth2Facade;
import com.lyl.study.cloud.wxclient.security.JwtClaims;
import com.lyl.study.cloud.wxclient.security.MemberAuthenticationToken;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class WxClientAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private String appId;
    private JwtSigner jwtSigner;
    private String verifyCodeSessionName;
    private String tokenCookieName;
    private String tokenCookiePath;
    private WxOAuth2Facade wxOAuth2Facade;

    public WxClientAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this(new AntPathRequestMatcher("/session/login", "POST"), authenticationEntryPoint);
    }

    public WxClientAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                        AuthenticationEntryPoint authenticationEntryPoint) {
        super(requiresAuthenticationRequestMatcher);
        setContinueChainBeforeSuccessfulAuthentication(false);

        setSessionAuthenticationStrategy(new AccountSessionAuthenticationStrategy(this));
        setAuthenticationSuccessHandler(new WxClientAuthenticationSuccessHandler());
        setAuthenticationFailureHandler(new WxClientAuthenticationFailureHandler(authenticationEntryPoint));
    }

    public String getAppId() {
        return appId;
    }

    public WxClientAuthenticationFilter setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getVerifyCodeSessionName() {
        return verifyCodeSessionName;
    }

    public WxClientAuthenticationFilter setVerifyCodeSessionName(String verifyCodeSessionName) {
        this.verifyCodeSessionName = verifyCodeSessionName;
        return this;
    }

    public WxOAuth2Facade getWxOAuth2Facade() {
        return wxOAuth2Facade;
    }

    public WxClientAuthenticationFilter setWxOAuth2Facade(WxOAuth2Facade wxOAuth2Facade) {
        this.wxOAuth2Facade = wxOAuth2Facade;
        return this;
    }

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public WxClientAuthenticationFilter setTokenCookieName(String tokenCookieName) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

    public JwtSigner getJwtSigner() {
        return jwtSigner;
    }

    public void setJwtSigner(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    public String getTokenCookiePath() {
        return tokenCookiePath;
    }

    public void setTokenCookiePath(String tokenCookiePath) {
        this.tokenCookiePath = tokenCookiePath;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        Assert.notNull(appId, "appId cannot be null");
        Assert.notNull(wxOAuth2Facade, "wxOAuth2Facade cannot be null");
        Assert.notNull(verifyCodeSessionName, "verifyCodeSessionName cannot be null");
        Assert.notNull(jwtSigner, "jwtSigner cannot be null");
        Assert.notNull(tokenCookieName, "tokenCookieName cannot be null");
        Assert.notNull(tokenCookiePath, "tokenCookiePath cannot be null");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        MediaType contentType = MediaType.valueOf(request.getContentType());

        // 只接受 Application/json
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            LoginForm loginForm = JsonUtils.fromJson(request.getInputStream(), LoginForm.class);

            if (log.isDebugEnabled()) {
                log.debug("接受到用户登录请求: {}", loginForm);
            }

            checkLoginForm(loginForm, request);

            MemberDTO member = new MemberDTO();
            member.setMobile(loginForm.mobile);

            MemberAuthenticationToken authentication = new MemberAuthenticationToken(member);
            authentication.setCode(loginForm.getCode());
            return getAuthenticationManager().authenticate(authentication);
        } else {
            throw new HttpMediaTypeNotSupportedException(contentType,
                    Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        }
    }

    protected void checkLoginForm(LoginForm loginForm, HttpServletRequest request) {
        if (loginForm.mobile == null || loginForm.verifyCode == null) {
            throw new InvalidArgumentException("请填写手机号、验证码");
        }

        // 校验验证码
        String verifyCodeInSession = (String) request.getSession().getAttribute(verifyCodeSessionName);
        if (!loginForm.verifyCode.toLowerCase().equals(verifyCodeInSession.toLowerCase())) {
            throw new InvalidArgumentException("验证码不正确");
        }
    }

    @Data
    @ToString
    private static class LoginForm {
        private String mobile;
        private String code;
        private String verifyCode;
    }

    private static class AccountSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
        private WxClientAuthenticationFilter filter;

        AccountSessionAuthenticationStrategy(WxClientAuthenticationFilter filter) {
            this.filter = filter;
        }

        @Override
        public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                     HttpServletResponse response) throws SessionAuthenticationException {
            MemberAuthenticationToken authenticationToken = (MemberAuthenticationToken) authentication;
            MemberDTO dto = (MemberDTO) authenticationToken.getDetails();

            // 构建Jwtliams
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setMemberId(dto.getId());
            jwtClaims.setLoginTime(System.currentTimeMillis());

            // 获取OpenID
            String code = ((MemberAuthenticationToken) authentication).getCode();
            if (code != null) {
                jwtClaims.setOpenId(filter.wxOAuth2Facade.oauth2getOpenId(filter.appId, code));
            }

            String jwt = filter.jwtSigner.serializeToken(jwtClaims);


            // RoleId用于标识当前用户切换的角色(刚登录的用户默认使用第一个角色)
            Cookie cookie = new Cookie(filter.tokenCookieName, jwt);
            if (filter.tokenCookiePath != null) {
                cookie.setPath(filter.tokenCookiePath);
            }
            cookie.setHttpOnly(true);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    private static class WxClientAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            HttpServletUtils.writeJson(HttpStatus.OK.value(),
                    new Result<>(CommonErrorCode.OK, "登录成功", null),
                    response);
        }
    }

    private static class WxClientAuthenticationFailureHandler implements AuthenticationFailureHandler {
        private AuthenticationEntryPoint authenticationEntryPoint;

        public WxClientAuthenticationFailureHandler(AuthenticationEntryPoint authenticationEntryPoint) {
            this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception) throws IOException, ServletException {
            authenticationEntryPoint.commence(request, response, exception);
        }
    }
}

