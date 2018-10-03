package com.lyl.study.cloud.base.idworker;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisIncrementSequence implements IncrementSequence {
    private RedisTemplate redisTemplate;

    public RedisIncrementSequence(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public long nextId(String incrementKey) {
        return redisTemplate.opsForValue().increment(incrementKey, 1);
    }
}
