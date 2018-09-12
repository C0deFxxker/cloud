package com.lyl.study.cloud.base.test.util;

import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.util.TreeNodeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeNodeUtilsTest extends Assert {
    private List<A> sampleList;
    private TreeNode<A> sampleRoot;

    @Before
    public void setup() {
        // Prepare sample list
        sampleList = new ArrayList<>(5);
        sampleList.add(new A(1, "node-1", null));
        sampleList.add(new A(2, "node-2", 1));
        sampleList.add(new A(3, "node-3", 1));
        sampleList.add(new A(4, "node-4", 2));
        sampleList.add(new A(5, "node-5", 2));

        // Prepare sample tree
        sampleRoot = new TreeNode<>();
        sampleRoot.setId(1);
        sampleRoot.setLabel("node-1");
        sampleRoot.setParentId(null);
        sampleRoot.setDetail(new A(1, "node-1", null));
        TreeNode<A> node1 = new TreeNode<>();
        node1.setId(2);
        node1.setLabel("node-2");
        node1.setParentId(1);
        node1.setDetail(new A(2, "node-2", 1));
        TreeNode<A> node2 = new TreeNode<>();
        node2.setId(3);
        node2.setLabel("node-3");
        node2.setParentId(1);
        node2.setDetail(new A(3, "node-3", 1));
        TreeNode<A> node3 = new TreeNode<>();
        node3.setId(4);
        node3.setLabel("node-4");
        node3.setParentId(2);
        node3.setDetail(new A(4, "node-4", 2));
        TreeNode<A> node4 = new TreeNode<>();
        node4.setId(5);
        node4.setLabel("node-5");
        node4.setParentId(2);
        node4.setDetail(new A(5, "node-5", 2));
        sampleRoot.getChildren().add(node1);
        sampleRoot.getChildren().add(node2);
        node1.getChildren().add(node3);
        node1.getChildren().add(node4);
    }

    @Test
    public void buildTree() {
        TreeNodeUtils.BuildTreeConfig<A> config = new TreeNodeUtils.BuildTreeConfig<>();
        config.setIdGetter(A::getId);
        config.setLabelGetter(A::getName);
        config.setParentIdGetter(A::getParentId);

        List<TreeNode<A>> trees = TreeNodeUtils.buildTree(sampleList, null, config);

        assertEquals(trees.size(), 1);
        TreeNode<A> root = trees.get(0);
        assertEquals(root.getId(), 1);
        assertEquals(root.getLabel(), "node-1");
        assertEquals(root.getChildren().size(), 2);
        TreeNode<A> node1 = root.getChildren().get(0);
        TreeNode<A> node2 = root.getChildren().get(1);
        assertEquals(node1.getLabel(), "node-2");
        assertEquals(node1.getId(), 2);
        assertEquals(node2.getLabel(), "node-3");
        assertEquals(node2.getId(), 3);
        assertEquals(node1.getChildren().size(), 2);
        assertEquals(node2.getChildren().size(), 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getDeep() {
        int deep = TreeNodeUtils.getDeep(sampleRoot);
        assertEquals(deep, 3);
    }

    @Test
    public void bfsWalkerTest() {
        final List<Integer> visitOrder = new ArrayList<>();
        TreeNodeUtils.bfsWalker(sampleRoot, ((node, parent, deep) -> {
            visitOrder.add((Integer) node.getId());
        }));
        assertEquals(visitOrder, Arrays.asList(1, 2, 3, 4, 5));
    }

    @Test
    public void dfsWalkerTest() {
        final List<Integer> visitOrder = new ArrayList<>();
        TreeNodeUtils.dfsWalker(sampleRoot, ((node, parent, deep) -> {
            visitOrder.add((Integer) node.getId());
        }));
        assertEquals(visitOrder, Arrays.asList(1, 2, 4, 5, 3));
    }

    private static class A {
        private int id;
        private String name;
        private Integer parentId;

        public A() {
        }

        public A(int id, String name, Integer parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getParentId() {
            return parentId;
        }
    }
}