package com.lyl.study.cloud.base.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.plugins.PerformanceInterceptor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Configurable
@ConditionalOnClass(name = "com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusAutoConfiguration")
public class MybatisCommonConfig {
    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLocalPage(true);
        return paginationInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "debug", havingValue = "true")
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
