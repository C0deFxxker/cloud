package com.lyl.study.cloud.base.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Redis分布锁实例类
 * 提供分布式线程锁功能
 * 保证分布式的环境下，有且仅有一台服务器上的一个线程获得该锁资源
 *
 * @author 黎毅麟
 */
public class RedisLock implements Lock {
    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private String key;

    private boolean locked;

    private int duration;

    private RedisTemplate<String, String> redisTemplate;

    public RedisLock(String key, int duration, RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.duration = duration;
        this.redisTemplate = redisTemplate;
        this.locked = false;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        boolean success = redisTemplate.opsForValue().setIfAbsent(key, "");
        while (!success) {
            logger.debug("锁资源获取失败，稍后重试");

            Thread.sleep(100);
            long now = System.currentTimeMillis();
            // 把单位转为秒
            if ((now - startTime) / 1000 >= timeout) {
                return false;
            }
            success = tryLock();
        }

        logger.debug("锁资源获取成功");
        redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        locked = true;
        return true;
    }

    public void lock(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        // 上锁
        if (!this.tryLock(timeout, unit)) {
            throw new TimeoutException("Redis锁获取超时: " + key);
        }
    }

    @Override
    public boolean tryLock() {
        return redisTemplate.opsForValue().setIfAbsent(key, "");
    }

    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void unlock() {
        redisTemplate.delete(key);
    }

    @Override
    public void lock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
