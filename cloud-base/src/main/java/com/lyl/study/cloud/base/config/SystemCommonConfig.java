package com.lyl.study.cloud.base.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lyl.study.cloud.base.util.JsonUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

        // 一些数值类型太大可能超出Js Number类型的有效数值范围，需要强转为字符串给前端
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        simpleModule.addSerializer(long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
//        om.registerModule(simpleModule);

        return om;
    }
}
