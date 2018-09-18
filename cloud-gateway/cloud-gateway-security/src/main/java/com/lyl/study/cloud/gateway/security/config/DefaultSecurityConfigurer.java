package com.lyl.study.cloud.gateway.security.config;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.gateway.api.facade.RoleFacade;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import com.lyl.study.cloud.gateway.security.DefaultAccessDeniedHandler;
import com.lyl.study.cloud.gateway.security.JsonAuthenticationEntryPoint;
import com.lyl.study.cloud.gateway.security.JwtSigner;
import com.lyl.study.cloud.gateway.security.filter.UserJwtConcurrentSessionFilter;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
public class DefaultSecurityConfigurer extends WebSecurityConfigurerAdapter {
    protected String[] permitUrls = new String[0];

    @Value("${cloud.security.token.cookie-name:token}")
    protected String tokenCookieName;
    @Value("${cloud.security.token.cookie-path:/}")
    protected String tokenCookiePath;
    @Value("${cloud.security.secret}")
    protected String jwtSecret;

    @Reference
    private UserFacade userFacade;
    @Reference
    private RoleFacade roleFacade;

    @Value("${cloud.security.permit-urls:''}")
    public void setPermitUrls(String permitUrlString) {
        if (!StringUtils.isEmpty(permitUrlString)) {
            permitUrls = StringUtils.tokenizeToStringArray(permitUrlString, ",; ");
        }

        log.info("permitUrls={}", Arrays.asList(permitUrls));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtSigner jwtSigner = getApplicationContext().getBean(JwtSigner.class);

        http
                .exceptionHandling()
                .authenticationEntryPoint(jsonAuthenticationEntryPoint())
                .accessDeniedHandler(defaultAccessDeniedHandler())
                .and()
                .authorizeRequests()
                .antMatchers(permitUrls).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAt(jwtConcurrentSessionFilter(jwtSigner), ConcurrentSessionFilter.class)
                .csrf().disable();
    }

    /**
     * JWT签名处理器
     */
    @Bean
    public JwtSigner jwtSigner(ObjectMapper objectMapper) {
        JwtSigner jwtSigner = new JwtSigner();
        jwtSigner.setSecret(jwtSecret);
        jwtSigner.setObjectMapper(objectMapper);
        return jwtSigner;
    }

    /**
     * 会话识别过滤器
     * 不添加@Bean，否则会被同时加到SpringApplicationFilterChain与SpringSecurityFilterChain导致该Filter被重复调用
     */
//    @Bean
    public UserJwtConcurrentSessionFilter jwtConcurrentSessionFilter(JwtSigner jwtSigner) {
        Assert.notNull(userFacade, "userFacade不能为空，请检查Dubbo服务是否启动并确保服务提供方已成功注册服务");
        Assert.notNull(roleFacade, "roleFacade不能为空，请检查Dubbo服务是否启动并确保服务提供方已成功注册服务");

        UserJwtConcurrentSessionFilter filter = new UserJwtConcurrentSessionFilter(jwtSigner);
        filter.setCookieName(tokenCookieName);
        filter.setCookiePath(tokenCookiePath);
        filter.setUserFacade(userFacade);
        filter.setRoleFacade(roleFacade);
        return filter;
    }

    /**
     * 认证入口点处理器
     */
    @Bean
    public JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return new JsonAuthenticationEntryPoint();
    }

    /**
     * 授权校验处理器
     */
    @Bean
    public DefaultAccessDeniedHandler defaultAccessDeniedHandler() {
        return new DefaultAccessDeniedHandler();
    }
}
