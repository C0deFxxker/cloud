package com.lyl.study.cloud.gateway.security.filter;

import com.lyl.study.cloud.gateway.security.JwtClaims;
import com.lyl.study.cloud.gateway.security.JwtSigner;
import com.lyl.study.cloud.gateway.security.UserAuthenticationToken;
import com.lyl.study.cloud.gateway.security.exception.InvalidJwtException;
import com.lyl.study.cloud.gateway.security.exception.InvalidRoleException;
import com.lyl.study.cloud.gateway.security.exception.JwtExpireException;
import com.lyl.study.cloud.security.api.dto.response.RoleDTO;
import com.lyl.study.cloud.security.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.security.api.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 基于JWT的会话用户识别过滤器
 *
 * @author liyilin
 */
public class UserJwtConcurrentSessionFilter extends GenericFilterBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Token有效期（秒）, 从登录开始计算
    private int sessionAge = 7200;
    // Token存放在Cookie中的名称
    private String cookieName = "token";
    // Token存放在Cookie中的Cookie-Path
    private String cookiePath = null;
    // JWT签名工具
    private JwtSigner jwtSigner;
    private UserFacade userFacade;

    public UserJwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(userFacade, "userFacade cannot be null");
        Assert.notNull(jwtSigner, "jwtSigner cannot be null");
        Assert.hasText(cookieName, "cookieName cannot be blank text or null");

        if (cookiePath != null && !cookiePath.startsWith("/")) {
            cookiePath = "/" + cookiePath;
        }

        logger.info("初始化完毕: CookieName={}, CookiePath={}, SessionAge={}", cookieName, cookiePath, sessionAge);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getToken((HttpServletRequest) servletRequest);

        // 如果JWT不为空，则进行识别操作
        if (!StringUtils.isEmpty(token)) {
            try {
                JwtClaims claims = jwtSigner.deserializeToken(token, JwtClaims.class);
                assertJwtClaimsValid(claims);

                UserDetailDTO user = resolveUser(claims);

                RoleDTO currentRole = resolveCurrentRole(claims, user);

                // 把用户信息记录到当前会话状态中
                UserAuthenticationToken authenticationToken = new UserAuthenticationToken(user, currentRole);
                authenticationToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                logger.error(e.getClass() + ": " + e.getMessage());
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 获取JWT的Token值
     */
    protected String getToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 校验JWT有效性
     */
    protected void assertJwtClaimsValid(JwtClaims jwtClaims) throws InvalidJwtException {
        boolean invalid = (jwtClaims.getUserId() == null
                || jwtClaims.getCurrentRoleId() == null
                || jwtClaims.getLoginTime() == null);

        if (invalid) {
            throw new InvalidJwtException("Jwt Claims invalid.");
        }

        long now = System.currentTimeMillis();
        if (now - jwtClaims.getLoginTime() >= sessionAge * 1000L) {
            throw new JwtExpireException("Jwt Expired.");
        }
    }

    /**
     * 获取当前会话的用户信息
     */
    protected UserDetailDTO resolveUser(JwtClaims jwtClaims) throws InvalidJwtException, NoSuchEntityException {
        Long userId = jwtClaims.getUserId();
        if (userId == null) {
            throw new InvalidJwtException("Invalid Token: userId cannot be null");
        }
        UserDetailDTO user = userFacade.getById(userId);
        if (user == null) {
            throw new NoSuchEntityException("Cannot find User[ID=" + userId + "]");
        }
        return user;
    }

    /**
     * 获取当前切换的角色信息
     */
    protected RoleDTO resolveCurrentRole(JwtClaims jwtClaims, UserDetailDTO user) throws InvalidRoleException {
        Long currentRoleId = jwtClaims.getCurrentRoleId();
        List<RoleDTO> roles = user.getRoles();
        Optional<RoleDTO> optional = roles.stream()
                .filter(entity -> entity.getId().equals(currentRoleId))
                .findFirst();
        if (!optional.isPresent()) {
            throw new InvalidRoleException("Invalid current role.");
        }
        return optional.get();
    }

    public String getCookieName() {
        return cookieName;
    }

    public UserJwtConcurrentSessionFilter setCookieName(String cookieName) {
        this.cookieName = cookieName;
        return this;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public UserJwtConcurrentSessionFilter setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
        return this;
    }

    public JwtSigner getJwtSigner() {
        return jwtSigner;
    }

    public void setJwtSigner(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public int getSessionAge() {
        return sessionAge;
    }

    public void setSessionAge(int sessionAge) {
        this.sessionAge = sessionAge;
    }
}
