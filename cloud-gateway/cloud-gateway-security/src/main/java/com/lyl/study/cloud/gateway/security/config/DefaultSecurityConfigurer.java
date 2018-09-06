package com.lyl.study.cloud.gateway.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.gateway.security.DefaultAccessDeniedHandler;
import com.lyl.study.cloud.gateway.security.JsonAuthenticationEntryPoint;
import com.lyl.study.cloud.gateway.security.JwtSigner;
import com.lyl.study.cloud.gateway.security.filter.UserJwtConcurrentSessionFilter;
import com.lyl.study.cloud.security.api.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Order(5)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
public class DefaultSecurityConfigurer extends WebSecurityConfigurerAdapter {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String[] permitUrls = new String[0];

    @Value("${cloud.security.session-age:7200}")
    protected Integer sessionAge;
    @Value("${cloud.security.token.cookie-name:token}")
    protected String tokenCookieName;
    @Value("${cloud.security.token.cookie-path:'/'}")
    protected String tokenCookiePath;
    @Value("${cloud.security.secret}")
    protected String jwtSecret;

    @Value("${cloud.security.permit-urls:''}")
    public void setPermitUrls(String permitUrlString) {
        if (!StringUtils.isEmpty(permitUrlString)) {
            permitUrls = StringUtils.tokenizeToStringArray(permitUrlString, ",; ");
        }

        logger.info("permitUrls={}", Arrays.asList(permitUrls));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UserJwtConcurrentSessionFilter filter = getApplicationContext().getBean(UserJwtConcurrentSessionFilter.class);

        http
                .exceptionHandling()
                .authenticationEntryPoint(jsonAuthenticationEntryPoint())
                .accessDeniedHandler(defaultAccessDeniedHandler())
                .and()
                .authorizeRequests()
                .antMatchers(permitUrls).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAt(filter, ConcurrentSessionFilter.class)
                .csrf().disable();
    }

    /**
     * JWT签名处理器
     */
    @Bean
    public JwtSigner jwtSigner(ObjectMapper objectMapper) {
        JwtSigner jwtSigner = new JwtSigner();
        jwtSigner.setSecret(jwtSecret);
        jwtSigner.setExpire(sessionAge);
        jwtSigner.setObjectMapper(objectMapper);
        return jwtSigner;
    }

    /**
     * 会话识别过滤器
     */
    @Bean
    public UserJwtConcurrentSessionFilter jwtConcurrentSessionFilter(JwtSigner jwtSigner, UserFacade userFacade) {
        UserJwtConcurrentSessionFilter filter = new UserJwtConcurrentSessionFilter(jwtSigner);
        filter.setUserFacade(userFacade);
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
