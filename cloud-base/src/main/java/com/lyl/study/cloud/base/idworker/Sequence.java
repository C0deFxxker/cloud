package com.lyl.study.cloud.base.idworker;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Snowflake算法
 *
 * @author liyilin
 */
public class Sequence {
    // 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
    private final long twepoch;
    // 机器标识位数
    public static final long SERVICE_ID_BITS = 10L;
    public static final long MAX_SERVICE_ID = -1L ^ (-1L << SERVICE_ID_BITS);
    // 毫秒内自增位(同一台机一毫秒内只能生成4个ID)
    private final long sequenceBits = 2L;
    // 时间戳左移动位
    private final long timestampLeftShift = sequenceBits + SERVICE_ID_BITS;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 机器标识id部分
    private long serviceId;
    // 并发控制
    private volatile long sequence = 0L;
    // 上次生产id时间戳
    private volatile long lastTimestamp = -1L;

    public Sequence(int serviceId, long twepoch) {
        this.serviceId = serviceId;
        this.twepoch = twepoch;

        if (serviceId > MAX_SERVICE_ID || serviceId < 0) {
            throw new IllegalArgumentException(String.format("service Id can't be greater than %d or less than 0", MAX_SERVICE_ID));
        }
    }

    /**
     * 获取下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        //闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            if (sequence < sequenceMask) {
                sequence = sequence + 1;
            } else {
                timestamp = tilNextMillis(lastTimestamp);
                sequence = ThreadLocalRandom.current().nextLong(0, 1);
            }
        } else {
            // 不同毫秒内，序列号置为 0 - 1 随机数
            sequence = ThreadLocalRandom.current().nextLong(0, 1);
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)    // 时间戳部分
                | (serviceId << sequenceBits)                   // 机器标识部分
                | sequence;                                     // 序列号部分
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
