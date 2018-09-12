package com.lyl.study.cloud.gateway.web.config;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.gateway.security.JwtSigner;
import com.lyl.study.cloud.gateway.security.config.DefaultSecurityConfigurer;
import com.lyl.study.cloud.gateway.web.security.AdminAuthenticationFilter;
import com.lyl.study.cloud.gateway.web.security.ResultLogoutSuccessHandler;
import com.lyl.study.cloud.gateway.web.security.UserAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Order(0)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GatewaySecurityConfigurer extends DefaultSecurityConfigurer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.security.sessionAge}")
    protected Integer sessionAge;
    @Value("${cloud.gateway.logoutUrl}")
    private String logoutUrl;
    @Value("${cloud.gateway.loginUrl}")
    private String loginUrl;

    @Reference
    private UserFacade userFacade;
    @Reference
    private RoleFacade roleFacade;
    @Autowired
    private JwtSigner jwtSigner;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http
                .logout().logoutUrl(logoutUrl)
                .deleteCookies(tokenCookieName).invalidateHttpSession(true).clearAuthentication(true)
                .logoutSuccessHandler(new ResultLogoutSuccessHandler())
                .and()
                .addFilterAt(gatewayAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(webAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider webAuthenticationProvider() {
        return new UserAuthenticationProvider(userFacade);
    }

    public AbstractAuthenticationProcessingFilter gatewayAuthenticationFilter() throws Exception {
        AdminAuthenticationFilter filter = new AdminAuthenticationFilter(
                new AntPathRequestMatcher(loginUrl, "POST"),
                jsonAuthenticationEntryPoint()
        );
        filter.setTokenCookieName(tokenCookieName);
        filter.setTokenCookiePath(tokenCookiePath);
        filter.setJwtSigner(jwtSigner);
        filter.setRoleFacade(roleFacade);
        filter.setUserFacade(userFacade);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Override
    public JwtSigner jwtSigner(ObjectMapper objectMapper) {
        JwtSigner jwtSigner = new JwtSigner();
        jwtSigner.setSecret(jwtSecret);
        jwtSigner.setExpire(sessionAge);
        jwtSigner.setObjectMapper(objectMapper);
        return jwtSigner;
    }
}