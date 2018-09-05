package com.lyl.study.cloud.security.core.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class JwtConcurrentSessionFilter<T> extends GenericFilterBean {
    private String cookieName = "token";
    private String cookiePath = null;
    private Class<T> claimsClass;

    public JwtConcurrentSessionFilter(Class<T> claimsClass) {
        this.claimsClass = claimsClass;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        
    }

    public Class<T> getClaimsClass() {
        return claimsClass;
    }

    public String getCookieName() {
        return cookieName;
    }

    public JwtConcurrentSessionFilter<T> setCookieName(String cookieName) {
        this.cookieName = cookieName;
        return this;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public JwtConcurrentSessionFilter<T> setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
        return this;
    }
}
