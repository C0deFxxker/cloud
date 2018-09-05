package com.lyl.study.cloud.base.config;


import com.lyl.study.cloud.base.idworker.IdWorkerRegister;
import com.lyl.study.cloud.base.idworker.RedisIdWorkerRegister;
import com.lyl.study.cloud.base.idworker.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @Bean
    @ConditionalOnBean(IdWorkerRegister.class)
    @ConditionalOnMissingBean(Sequence.class)
    public Sequence sequence(IdWorkerRegister idWorkerRegister) {
        int serviceId = idWorkerRegister.registerServiceId();
        long twepoch = idWorkerRegister.getTwepoch();
        logger.info("使用IdWorkerRegister注册: serviceId={}, twepoch={}", serviceId, twepoch);
        return new Sequence(serviceId, twepoch);
    }

    @Bean
    @ConditionalOnMissingBean({IdWorkerRegister.class, Sequence.class})
    public Sequence sequence() {
        long twepoch = System.currentTimeMillis();
        return new Sequence(1, twepoch);
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
