package com.lyl.study.cloud.base.idworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

public class RedisIdWorkerRegister implements IdWorkerRegister {
    private static final Logger logger = LoggerFactory.getLogger(RedisIdWorkerRegister.class);

    private String prefix;
    private RedisTemplate<String, Object> redisTemplate;
    private int maxServiceId = 1024;

    public RedisIdWorkerRegister(String prefix, RedisTemplate<String, Object> redisTemplate) {
        this.prefix = prefix;
        this.redisTemplate = redisTemplate;
    }

    public RedisIdWorkerRegister(String prefix, RedisTemplate<String, Object> redisTemplate, int maxServiceId) {
        this.prefix = prefix;
        this.redisTemplate = redisTemplate;
        this.maxServiceId = maxServiceId;
    }

    @Override
    public int registerServiceId() {
        Integer serviceId;
        try {
            Long mac = getLongMacAddress();
            if (mac == null) {
                serviceId = new Random().nextInt(maxServiceId);
            } else {
                serviceId = getExistsServiceId(mac);
                if (serviceId == null) {
                    serviceId = doRegisterServiceId(mac);
                }
            }
            logger.info("ServiceId: " + serviceId);
            return serviceId;
        } catch (Exception e) {
            logger.error("getDatacenterId: " + e.getMessage());
            logger.info("ServiceId: 0");
        }
        return 0;
    }

    @Override
    public long getTwepoch() {
        String key = getTwepochKey(prefix);
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            return Long.parseLong(o.toString());
        } else {
            Date date = new Date();
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, date.getTime());
            if (!success) {
                return getTwepoch();
            } else {
                return date.getTime();
            }
        }
    }

    protected Long getLongMacAddress() throws SocketException, UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        if (network == null) {
            return null;
        } else {
            byte[] mac = network.getHardwareAddress();
            long macLong = 0L;
            if (null != mac) {
                for (int i = 0; i < mac.length; i++) {
                    int idx = mac.length - 1 - i;
                    macLong |= (long) mac[idx] << idx;
                }
            }
            return macLong;
        }
    }

    protected Integer getExistsServiceId(long mac) {
        Object o = redisTemplate.opsForHash().get(getServiceIdHashKey(prefix), mac);
        return o != null ? Integer.parseInt(o.toString()) : null;
    }

    protected int doRegisterServiceId(long mac) {
        Long serviceId = redisTemplate.opsForValue().increment(getServiceCountKey(prefix), 1L);
        redisTemplate.opsForHash().put(getServiceIdHashKey(prefix), mac, serviceId);
        return serviceId.intValue();
    }

    protected String getServiceIdHashKey(String prefix) {
        return prefix + ":idWorker:map";
    }

    protected String getServiceCountKey(String prefix) {
        return prefix + ":idWorker:count";
    }

    protected String getTwepochKey(String prefix) {
        return prefix + ":idWorker:twepoch";
    }
}
