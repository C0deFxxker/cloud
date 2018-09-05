package com.lyl.study.cloud.base.util;

import java.util.*;

/**
 * @author 黎毅麟
 */
public class CollectionUtils {

    /**
     * 设置内存分页数据
     *
     * @param list      列表
     * @param pageIndex 页码(从1开始)
     * @param pageSize  页面大小
     * @return 分页后的列表
     */
    public static <T> List<T> pageList(List<T> list, int pageIndex, int pageSize) {
        int start = (pageIndex - 1) * pageSize;
        int end = pageIndex * pageSize;

        if (end > list.size()) {
            end = list.size();
        }

        if (start >= list.size() || start >= end) {
            return new ArrayList<>();
        }

        return list.subList(start, end);
    }

    /**
     * 把多个Map融合成一个Map，如果多个Map存在相同的Key，则取参数顺序最后面Map的Key的值
     *
     * @param mapList Map列表
     * @param <K>     键类型
     * @param <V>     值类型
     * @return 融合之后的Map
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mapMerge(Map<K, V>... mapList) {
        Map<K, V> result = null;
        if (mapList != null) {
            result = new HashMap<>(mapList.length);
            for (Map<K, V> each : mapList) {
                for (K key : each.keySet()) {
                    result.put(key, each.get(key));
                }
            }
        }
        return result;
    }

    /**
     * 计算差集: a - b
     *
     * @param a   a集合
     * @param b   b集合
     * @param <T> 集合元素类型
     * @return a集合与b集合的差集
     */
    public static <T> Set<T> subSet(Set<T> a, Set<T> b) {
        Set<T> diffSet = new HashSet<>();
        for (T each : a) {
            if (!b.contains(each)) {
                diffSet.add(each);
            }
        }
        return diffSet;
    }
}
