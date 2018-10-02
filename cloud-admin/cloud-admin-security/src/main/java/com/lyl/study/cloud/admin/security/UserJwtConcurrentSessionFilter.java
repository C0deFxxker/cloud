package com.lyl.study.cloud.admin.security;

import com.lyl.study.cloud.admin.security.exception.InvalidRoleException;
import com.lyl.study.cloud.base.security.jwt.JwtConcurrentSessionFilter;
import com.lyl.study.cloud.base.security.jwt.JwtSigner;
import com.lyl.study.cloud.base.exception.InvalidJwtException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.response.RoleDTO;
import com.lyl.study.cloud.system.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.system.api.facade.RoleFacade;
import com.lyl.study.cloud.system.api.facade.UserFacade;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import java.util.List;

/**
 * 基于JWT的会话用户识别过滤器
 *
 * @author liyilin
 */
@Slf4j
public class UserJwtConcurrentSessionFilter extends JwtConcurrentSessionFilter {
    // 用户管理服务
    private UserFacade userFacade;
    // 角色管理服务
    private RoleFacade roleFacade;

    public UserJwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        super(jwtSigner);
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
        boolean invalid = (jwtClaims.getUserId() == null
                || jwtClaims.getCurrentRoleId() == null
                || jwtClaims.getLoginTime() == null);
        if (invalid) {
            throw new InvalidJwtException("Jwt Claims invalid, [userId, currentRoleId, loginTime]");
        }
    }

    @Override
    protected Authentication resolveAuthentication(Object claims) {
        JwtClaims jwtClaims = (JwtClaims) claims;
        UserDetailDTO user = resolveUser(jwtClaims);
        RoleDTO currentRole = resolveCurrentRole(jwtClaims, user);
        return new UserAuthenticationToken(user, currentRole);
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
            throw new NoSuchEntityException("找不到当前登录的用户信息，请重新登录");
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
}
