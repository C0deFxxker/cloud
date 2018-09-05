package com.lyl.study.cloud.base.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lyl.study.cloud.base.CacheNames;
import com.lyl.study.cloud.base.idworker.IdWorkerRegister;
import com.lyl.study.cloud.base.idworker.RedisIdWorkerRegister;
import com.lyl.study.cloud.base.lock.RedisLockAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置类
 * 当配置项 "spring.redis.lock.enable" 为 true 时，才会创建 Redis 分布式锁相关的 Bean
 *
 * @author 黎毅麟
 */
@Configuration
@EnableCaching
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class CacheCommonConfig {
    /**
     * 缓存超时时间（秒）
     */
    @Value("${spring.cache.default-expire:3600}")
    protected long defaultExpireTime;

    @Value("${spring.application.name}")
    protected String applicationName;

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setCachePrefix(cacheName -> (applicationName + ":" + cacheName).getBytes());
        cacheManager.setUsePrefix(true);
        // 设置缓存过期时间
        cacheManager.setDefaultExpiration(defaultExpireTime);
        // 设置缓存时间
        Map<String, Long> expires = new HashMap<>();
        expires.put(CacheNames.CACHE_NAME_ONE_HOUR, CacheNames.EXPIRE_ONE_HOUR);
        expires.put(CacheNames.CACHE_NAME_TWO_HOUR, CacheNames.EXPIRE_TWO_HOUR);
        expires.put(CacheNames.CACHE_NAME_FOUR_HOUR, CacheNames.EXPIRE_FOUR_HOUR);
        expires.put(CacheNames.CACHE_NAME_EIGHT_HOUR, CacheNames.EXPIRE_EIGHT_HOUR);
        expires.put(CacheNames.CACHE_NAME_HALF_DAY, CacheNames.EXPIRE_HALF_DAY);
        expires.put(CacheNames.CACHE_NAME_ONE_DAY, CacheNames.EXPIRE_ONE_DAY);
        expires.put(CacheNames.CACHE_NAME_TWO_DAY, CacheNames.EXPIRE_TWO_DAY);
        cacheManager.setExpires(expires);
        return cacheManager;
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 键序列化器设置
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 值序列化器设置
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.ProceedingJoinPoint")
    @ConditionalOnProperty(name = "system.cluster-lock.enable", havingValue = "true")
    public RedisLockAware redisLockAware() {
        return new RedisLockAware();
    }
}
