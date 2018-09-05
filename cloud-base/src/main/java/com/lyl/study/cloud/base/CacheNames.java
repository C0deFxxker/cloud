package com.lyl.study.cloud.base;

import com.lyl.study.cloud.base.util.DateUtils;

public class CacheNames {
    public static final String CACHE_NAME_ONE_HOUR = "oneHour";
    public static final String CACHE_NAME_TWO_HOUR = "twoHours";
    public static final String CACHE_NAME_FOUR_HOUR = "fourHours";
    public static final String CACHE_NAME_EIGHT_HOUR = "eightHours";
    public static final String CACHE_NAME_HALF_DAY = "halfDay";
    public static final String CACHE_NAME_ONE_DAY = "oneDay";
    public static final String CACHE_NAME_TWO_DAY = "twoDays";

    public static final long EXPIRE_ONE_HOUR = DateUtils.ONE_HOUR_OF_MILISECOND;
    public static final long EXPIRE_TWO_HOUR = EXPIRE_ONE_HOUR * 2;
    public static final long EXPIRE_FOUR_HOUR = EXPIRE_TWO_HOUR * 2;
    public static final long EXPIRE_EIGHT_HOUR = EXPIRE_FOUR_HOUR * 2;
    public static final long EXPIRE_HALF_DAY = EXPIRE_FOUR_HOUR * 3;
    public static final long EXPIRE_ONE_DAY = EXPIRE_HALF_DAY * 2;
    public static final long EXPIRE_TWO_DAY = EXPIRE_ONE_DAY * 2;
}
