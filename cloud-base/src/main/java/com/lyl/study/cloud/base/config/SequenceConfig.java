package com.lyl.study.cloud.base.config;


import com.lyl.study.cloud.base.idworker.IdWorkerRegister;
import com.lyl.study.cloud.base.idworker.RedisIdWorkerRegister;
import com.lyl.study.cloud.base.idworker.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liyilin
 */
@Configuration
@AutoConfigureAfter(SystemCommonConfig.class)
public class SequenceConfig {
    private static final Logger logger = LoggerFactory.getLogger(SequenceConfig.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(Sequence.class)
    public Sequence sequence() {
        int serviceId = 1;
        long twepoch = System.currentTimeMillis();
        try {
            IdWorkerRegister idWorkerRegister = applicationContext.getBean(IdWorkerRegister.class);
            serviceId = idWorkerRegister.registerServiceId();
            twepoch = idWorkerRegister.getTwepoch();
            logger.info("使用IdWorkerRegister注册: serviceId={}, twepoch={}", serviceId, twepoch);
        } catch (NoSuchBeanDefinitionException e) {
            logger.info("找不到IdWorkerRegister，使用本地版本的Sequence: serviceId={}, twepoch={}", serviceId, twepoch);
        }
        return new Sequence(serviceId, twepoch);
    }

    @Configuration
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    public static class RedisSequence {
        @Value("${system.idworker.key-prefix:''}")
        private String keyPrefix;

        @Bean
        public IdWorkerRegister idWorkerRegister(RedisTemplate redisTemplate) {
            return new RedisIdWorkerRegister(keyPrefix, redisTemplate);
        }
    }
}
