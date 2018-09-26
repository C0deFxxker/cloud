package com.lyl.study.cloud.base.util;

public class BeanUtils extends org.springframework.beans.BeanUtils {
    public static <T> T transform(Object source, Class<T> clazz) {
        return transform(source, clazz, (String[]) null);
    }

    public static <T> T transform(Object source, Class<T> clazz, String... properties) {
        T instance = instantiate(clazz);
        copyProperties(source, instance, properties);
        return instance;
    }

    public static <T> T transform(Object source, T target, Class<?> editable) {
        return transform(source, target, editable);
    }

    public static <T> T transform(Object source, T target) {
        copyProperties(source, target);
        return target;
    }
}
