package com.lyl.study.cloud.base.config;

import com.lyl.study.cloud.base.feign.ModelAttributeFeignInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignCommonConfig {
    /**
     * 让Feign可以进行Get方法的@ModelAttribute形式传参
     */
    @Bean
    public ModelAttributeFeignInterceptor modelAttributeFeignInterceptor() {
        return new ModelAttributeFeignInterceptor();
    }
}
