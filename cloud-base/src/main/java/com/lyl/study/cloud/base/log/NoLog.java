package com.lyl.study.cloud.base.log;

public @interface NoLog {
    boolean params() default false;

    boolean result() default false;

    boolean except() default false;
}