package com.lyl.study.cloud.base.config.annotation;

import com.lyl.study.cloud.base.config.SystemCommonConfig;

import java.lang.annotation.*;

/**
 * 启动这个注解后，框架会自动创建Sequnce实例
 *
 * @author liyilin
 * @see SystemCommonConfig
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableSequence {
}
