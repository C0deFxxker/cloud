package com.lyl.study.cloud.base.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class TreeNode<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Object id;

    /**
     * 标识
     */
    private String label;

    /**
     * 父节点ID
     */
    private Object parentId;

    /**
     * 节点内容
     */
    private T data;

    /**
     * 子节点
     */
    private List<TreeNode<T>> children = new ArrayList<>();

    @Override
    public String toString() {
        return "TreeNode{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", parentId=" + parentId +
                ", data=" + data +
                ", children=" + children +
                '}';
    }
}
