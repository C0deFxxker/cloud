package com.lyl.study.cloud.base.idworker;

/**
 * Snowflake算法
 *
 * @author liyilin
 */
public interface Sequence {
    long nextId();
}
