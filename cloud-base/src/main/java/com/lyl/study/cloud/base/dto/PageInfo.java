package com.lyl.study.cloud.base.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


public class PageInfo<T> implements Serializable {
    /**
     * 总数
     */
    private int total = 0;

    /**
     * 每页显示条数，默认 10
     */
    private int size = 10;

    /**
     * 总页数
     */
    private int pages = 0;

    /**
     * 当前页
     */
    private int current = 1;

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    public PageInfo() {
    }

    public PageInfo(int current, int size) {
        this.size = size;
        this.current = current;
    }

    public PageInfo(int current, int size, int total, List<T> records) {
        this.total = total;
        this.size = size;
        this.current = current;
        this.records = records;
        this.pages = (total + size - 1) / size;
    }

    public int getTotal() {
        return total;
    }

    public PageInfo<T> setTotal(int total) {
        this.total = total;
        pages = (total + size - 1) / size;
        return this;
    }

    public int getSize() {
        return size;
    }

    public PageInfo<T> setSize(int size) {
        this.size = size;
        pages = (total + size - 1) / size;
        return this;
    }

    public int getPages() {
        return pages;
    }

    public int getCurrent() {
        return current;
    }

    public PageInfo<T> setCurrent(int current) {
        this.current = current;
        return this;
    }

    public List<T> getRecords() {
        return records;
    }

    public PageInfo<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }
}
