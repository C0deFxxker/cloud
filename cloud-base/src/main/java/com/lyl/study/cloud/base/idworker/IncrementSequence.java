package com.lyl.study.cloud.base.idworker;

public interface IncrementSequence {
    long nextId(String incrementKey);
}
