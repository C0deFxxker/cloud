package com.lyl.study.cloud.base.idworker;

public interface IdWorkerRegister {
    int registerServiceId();

    /**
     * 获取IdWorker起始基准时间，一旦确认不能改变
     *
     * @return 基准时间的时间戳
     */
    long getTwepoch();
}
