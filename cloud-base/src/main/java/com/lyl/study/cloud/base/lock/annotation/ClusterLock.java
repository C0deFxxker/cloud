package com.lyl.study.cloud.base.lock.annotation;

import java.lang.annotation.*;

/**
 * 为方法打上该注解，在进入方法时会自动申请Redis锁，在退出方法时，会自动释放Redis锁
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClusterLock {
    /**
     * Redis中的Key值(支持SpEL表达式)
     */
    String value();

    /**
     * 获取锁等待超时时间，默认为 120 秒
     */
    int timeout() default 120;

    /**
     * 锁持有时长，超过该时长将自动释放Redis锁，默认为 300 秒
     */
    int duration() default 300;
}
