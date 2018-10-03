package com.lyl.study.cloud.base.security.jwt;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.BaseException;
import com.lyl.study.cloud.base.exception.InvalidJwtException;
import com.lyl.study.cloud.base.util.HttpServletUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import java.util.Objects;

/**
 * 基于JWT的会话用户识别过滤器
 *
 * @author liyilin
 */
@Slf4j
public abstract class JwtConcurrentSessionFilter extends GenericFilterBean {
    // Token存放在Cookie中的名称
    protected String cookieName = "token";
    // Token存放在Cookie中的Cookie-Path
    protected String cookiePath = null;
    // JWT签名工具
    protected JwtSigner jwtSigner;

    public JwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(jwtSigner, "jwtSigner cannot be null");
        Assert.hasText(cookieName, "cookieName cannot be blank text or null");

        if (cookiePath != null && !cookiePath.startsWith("/")) {
            cookiePath = "/" + cookiePath;
        }

        log.info("初始化完毕: CookieName={}, CookiePath={}", cookieName, cookiePath);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getToken((HttpServletRequest) servletRequest);

        // 如果JWT不为空，则进行识别操作
        if (!StringUtils.isEmpty(token)) {
            try {
                Object claims = deserializeToken(token);

                if (log.isDebugEnabled()) {
                    log.debug("JWT内容: {}", claims.toString());
                }


                if (claims != null) {
                    assertJwtClaimsValid(claims);

                    Authentication authentication = resolveAuthentication(claims);

                    // 如果不为空，则认为已经登录
                    if (authentication != null) {
                        authentication.setAuthenticated(true);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException e) {
                if (log.isDebugEnabled()) {
                    log.debug("会话超时: " + token);
                }
//                Result<?> result = new Result<>(CommonErrorCode.EXPIRED_SESSION, "会话超时", null);
//                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
//                return;
            } catch (InvalidJwtException e) {
                if (log.isDebugEnabled()) {
                    log.debug("无效Token: " + e.getMessage());
                }
//                Result<?> result = new Result<>(CommonErrorCode.INVALD_JWT, "无效Token", null);
//                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
//                return;
            } catch (BaseException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.toString());
                }
                Result<?> result = e.toResult();
                HttpServletUtils.writeJson(HttpStatus.OK.value(), result, (HttpServletResponse) servletResponse);
                return;
            } catch (Exception e) {
                log.error(e.toString());
                Result<?> result = new Result<>(CommonErrorCode.INTERNAL_ERROR, "内部错误", e.getMessage());
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
     * 解析Token
     *
     * @param token Token串
     * @return 解析结果
     */
    protected abstract Object deserializeToken(String token) throws InvalidJwtException, ExpiredJwtException;

    /**
     * 校验JWT有效性
     */
    protected abstract void assertJwtClaimsValid(Object claims) throws InvalidJwtException;

    /**
     * 根据JWT获取认证对象
     *
     * @param claims jwt解析对象
     * @return 返回解析后的认证对象；若为null，则表示当前尚未登录。
     */
    protected abstract Authentication resolveAuthentication(Object claims);

    public String getCookieName() {
        return cookieName;
    }

    public JwtConcurrentSessionFilter setCookieName(String cookieName) {
        this.cookieName = cookieName;
        return this;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public JwtConcurrentSessionFilter setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
        return this;
    }

    public JwtSigner getJwtSigner() {
        return jwtSigner;
    }

    public void setJwtSigner(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }
}
