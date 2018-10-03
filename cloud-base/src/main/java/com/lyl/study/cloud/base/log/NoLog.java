package com.lyl.study.cloud.base.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLog {
    /**
     * 不记录入参
     */
    boolean params() default false;

    /**
     * 不记录返回值
     */
    boolean result() default false;

    /**
     * 不记录异常
     */
    boolean except() default false;
}