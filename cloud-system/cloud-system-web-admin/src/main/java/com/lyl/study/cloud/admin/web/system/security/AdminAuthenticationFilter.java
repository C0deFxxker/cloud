package com.lyl.study.cloud.admin.web.system.security;

import com.google.common.base.Charsets;
import com.lyl.study.cloud.admin.security.JwtClaims;
import com.lyl.study.cloud.admin.security.UserAuthenticationToken;
import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.security.jwt.JwtSigner;
import com.lyl.study.cloud.base.util.CryptoUtils;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import com.lyl.study.cloud.base.util.JsonUtils;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.system.api.facade.RoleFacade;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
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
import java.util.List;
import java.util.Optional;

@Slf4j
public class AdminAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private RoleFacade roleFacade;
    private JwtSigner jwtSigner;
    private String tokenCookieName;
    private String tokenCookiePath;

    public AdminAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this(new AntPathRequestMatcher("/session/login", "POST"), authenticationEntryPoint);
    }

    public AdminAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                     AuthenticationEntryPoint authenticationEntryPoint) {
        super(requiresAuthenticationRequestMatcher);
        setContinueChainBeforeSuccessfulAuthentication(false);

        setSessionAuthenticationStrategy(new AccountSessionAuthenticationStrategy(this));
        setAuthenticationSuccessHandler(new AdminAuthenticationSuccessHandler());
        setAuthenticationFailureHandler(new AdminAuthenticationFailureHandler(authenticationEntryPoint));
    }

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public AdminAuthenticationFilter setTokenCookieName(String tokenCookieName) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

    public RoleFacade getRoleFacade() {
        return roleFacade;
    }

    public void setRoleFacade(RoleFacade roleFacade) {
        this.roleFacade = roleFacade;
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

        Assert.notNull(roleFacade, "roleFacade cannot be null");
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

            checkLoginForm(loginForm);

            // 对密码加密
            loginForm.password = encryptPassword(loginForm.username, loginForm.password);

            UserDetailDTO user = new UserDetailDTO();
            user.setUsername(loginForm.username);
            user.setPassword(loginForm.password);

            Authentication authentication = new UserAuthenticationToken(user, null);
            return getAuthenticationManager().authenticate(authentication);
        } else {
            throw new HttpMediaTypeNotSupportedException(contentType,
                    Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        }
    }

    protected void checkLoginForm(LoginForm loginForm) {
        if (loginForm.username == null || loginForm.password == null) {
            throw new IllegalArgumentException("请填写用户名、密码");
        }
    }

    /**
     * 对账号密码进行加密
     *
     * @param account  账号
     * @param password 密码
     * @return 加密后的密码
     */
    protected static String encryptPassword(String account, String password) {
        String hmacSha1 = CryptoUtils.hmacSha1(account, password);
        return CryptoUtils.md5String(hmacSha1.getBytes(Charsets.UTF_8));
    }

    @Data
    @ToString
    private static class LoginForm {
        private String username;
        private String password;
    }

    private static class AccountSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
        private AdminAuthenticationFilter filter;

        AccountSessionAuthenticationStrategy(AdminAuthenticationFilter filter) {
            this.filter = filter;
        }

        @Override
        public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                     HttpServletResponse response) throws SessionAuthenticationException {
            UserAuthenticationToken webAuthenticationToken = (UserAuthenticationToken) authentication;
            UserDetailDTO dto = (UserDetailDTO) webAuthenticationToken.getDetails();
            Long currentRoleId = getCurrentRoleId(dto);

            // 构建Jwtliams
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setUserId(dto.getId());
            jwtClaims.setLoginTime(System.currentTimeMillis());
            jwtClaims.setCurrentRoleId(currentRoleId);
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

        private long getCurrentRoleId(UserDetailDTO dto) throws InsufficientAuthenticationException {
            List<RoleDTO> roles = dto.getRoles();
            Optional<RoleDTO> first = roles.stream().filter(RoleDTO::getEnable).findFirst();
            return first.orElseThrow(() -> new InsufficientAuthenticationException("该用户没有角色")).getId();
        }
    }

    private static class AdminAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            HttpServletUtils.writeJson(200,
                    new Result<>(CommonErrorCode.OK, "登录成功", null),
                    response);
        }
    }

    private static class AdminAuthenticationFailureHandler implements AuthenticationFailureHandler {
        private AuthenticationEntryPoint authenticationEntryPoint;

        public AdminAuthenticationFailureHandler(AuthenticationEntryPoint authenticationEntryPoint) {
            this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception) throws IOException, ServletException {
            authenticationEntryPoint.commence(request, response, exception);
        }
    }
}

