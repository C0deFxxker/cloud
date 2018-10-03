package com.lyl.study.cloud.base.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lyl.study.cloud.base.log.CommonLoggerAspect;
import com.lyl.study.cloud.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration")
public class SystemCommonConfig {
    @Configuration
    @ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
    static class RestTemplateConfig {
        @Bean
        @ConditionalOnMissingBean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public JsonUtils jsonUtils(ObjectMapper objectMapper) {
        return new JsonUtils(objectMapper);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        // 遇到未知属性不报错
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 遇到空集合不报错
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return om;
    }

    @Configuration
    @ConditionalOnClass(Aspect.class)
    @Import(CommonLoggerAspect.class)
    static class CommonLoggerAspectConfigurer {

    }
}