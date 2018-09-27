package com.lyl.study.cloud.admin.security.filter;

import com.lyl.study.cloud.admin.security.UserAuthenticationToken;
import com.lyl.study.cloud.admin.security.exception.InvalidJwtException;
import com.lyl.study.cloud.admin.security.exception.InvalidRoleException;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import com.lyl.study.cloud.gateway.api.GatewayErrorCode;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.admin.security.JwtClaims;
import com.lyl.study.cloud.admin.security.JwtSigner;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 基于JWT的会话用户识别过滤器
 *
 * @author liyilin
 */
@Slf4j
public class UserJwtConcurrentSessionFilter extends GenericFilterBean {
    // Token有效期（秒）, 从登录开始计算
    private int sessionAge = 7200;
    // Token存放在Cookie中的名称
    private String cookieName = "token";
    // Token存放在Cookie中的Cookie-Path
    private String cookiePath = null;
    // JWT签名工具
    private JwtSigner jwtSigner;
    // 用户管理服务
    private UserFacade userFacade;
    // 角色管理服务
    private RoleFacade roleFacade;

    public UserJwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(userFacade, "userFacade cannot be null");
        Assert.notNull(roleFacade, "roleFacade cannot be null");
        Assert.notNull(jwtSigner, "jwtSigner cannot be null");
        Assert.hasText(cookieName, "cookieName cannot be blank text or null");

        if (cookiePath != null && !cookiePath.startsWith("/")) {
            cookiePath = "/" + cookiePath;
        }

        log.info("初始化完毕: CookieName={}, CookiePath={}, SessionAge={}", cookieName, cookiePath, sessionAge);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getToken((HttpServletRequest) servletRequest);

        // 如果JWT不为空，则进行识别操作
        if (!StringUtils.isEmpty(token)) {
            try {
                JwtClaims claims = jwtSigner.deserializeToken(token, JwtClaims.class);

                if (log.isDebugEnabled()) {
                    log.debug("JWT内容: {}", claims);
                }

                assertJwtClaimsValid(claims);

                UserDetailDTO user = resolveUser(claims);

                RoleDTO currentRole = resolveCurrentRole(claims, user);

                // 把用户信息记录到当前会话状态中
                UserAuthenticationToken authenticationToken = new UserAuthenticationToken(user, currentRole);
                authenticationToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (ExpiredJwtException e) {
                if (log.isDebugEnabled()) {
                    log.debug("会话超时: " + token);
                }
                Result<?> result = new Result<>(GatewayErrorCode.EXPIRED_SESSION, "会话超时", null);
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
            } catch (InvalidJwtException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.toString());
                }
                Result<?> result = new Result<>(GatewayErrorCode.INVALD_JWT, "无效Token", null);
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
            } catch (InvalidRoleException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.toString());
                }
                Result<?> result = new Result<>(GatewayErrorCode.INVALD_ROLE, e.getMessage(), null);
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
            } catch (NoSuchEntityException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.toString());
                }
                Result<?> result = new Result<>(GatewayErrorCode.EXPIRED_SESSION, e.getMessage(), null);
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
            } catch (Exception e) {
                log.error(e.toString());
                Result<?> result = new Result<>(GatewayErrorCode.INTERNAL_ERROR, "内部错误", e.getMessage());
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
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
                if (Objects.equals(cookie.getName(), cookieName)) {
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
            throw new InvalidJwtException("Jwt Claims invalid, [userId, currentRoleId, loginTime, ");
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
            throw new NoSuchEntityException(GatewayErrorCode.NOT_FOUND, "找不到当前登录的用户信息，请重新登录");
        }
        user.setPassword(null);
        return user;
    }

    /**
     * 获取当前切换的角色信息
     */
    protected RoleDTO resolveCurrentRole(JwtClaims jwtClaims, UserDetailDTO user) throws InvalidRoleException {
        Long currentRoleId = jwtClaims.getCurrentRoleId();
        List<RoleDTO> roles = user.getRoles();
        return roles.stream()
                // 筛选Token中指定的角色
                .filter(entity -> entity.getId().equals(currentRoleId))
                .findFirst()
                // 数据库中查询对应的菜单
                .map(entity -> roleFacade.getById(entity.getId()))
                .orElseThrow(() -> new InvalidRoleException("当前角色无效"));
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

    public RoleFacade getRoleFacade() {
        return roleFacade;
    }

    public void setRoleFacade(RoleFacade roleFacade) {
        this.roleFacade = roleFacade;
    }

    public int getSessionAge() {
        return sessionAge;
    }

    public void setSessionAge(int sessionAge) {
        this.sessionAge = sessionAge;
    }
}
