package com.lyl.study.cloud.admin.web.system.config;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.admin.web.system.security.AdminAuthenticationFilter;
import com.lyl.study.cloud.admin.web.system.security.ResultLogoutSuccessHandler;
import com.lyl.study.cloud.admin.web.system.security.UserAuthenticationProvider;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.base.security.jwt.JwtSigner;
import com.lyl.study.cloud.admin.security.DefaultSecurityConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AdminSecurityConfigurer extends DefaultSecurityConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(AdminSecurityConfigurer.class);

    @Value("${cloud.security.sessionAge}")
    protected Integer sessionAge;
    @Value("${cloud.security.logoutUri}")
    private String logoutUri;
    @Value("${cloud.security.loginUri}")
    private String loginUri;

    @Reference
    private UserFacade userFacade;
    @Reference
    private RoleFacade roleFacade;
    @Autowired
    private JwtSigner jwtSigner;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("加载到的会话接口配置: loginUri={}, logoutUri={}, sessionAge={}", loginUri, logoutUri, sessionAge);

        super.configure(http);

        http
                .logout().logoutUrl(logoutUri)
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
                new AntPathRequestMatcher(loginUri, "POST"),
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