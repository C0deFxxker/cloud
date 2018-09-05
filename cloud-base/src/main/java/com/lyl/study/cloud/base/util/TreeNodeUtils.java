
package com.lyl.study.cloud.base.util;


import com.lyl.study.cloud.base.dto.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 建树工具类
 *
 * @author 黎毅麟
 */
public class TreeNodeUtils {
    /**
     * 建树配置类，配置建树时对每个元素部分属性的Getter方法
     *
     * @param <T> 泛型类
     */
    public static class BuildTreeConfig<T> {
        private Function<T, Object> idGetter;
        private Function<T, Object> parentIdGetter;
        private Function<T, String> labelGetter;

        public Function<T, Object> getIdGetter() {
            return idGetter;
        }

        public void setIdGetter(Function<T, Object> idGetter) {
            this.idGetter = idGetter;
        }

        public Function<T, Object> getParentIdGetter() {
            return parentIdGetter;
        }

        public void setParentIdGetter(Function<T, Object> parentIdGetter) {
            this.parentIdGetter = parentIdGetter;
        }

        public Function<T, String> getLabelGetter() {
            return labelGetter;
        }

        public void setLabelGetter(Function<T, String> labelGetter) {
            this.labelGetter = labelGetter;
        }
    }

    /**
     * 把列表中的元素转换为树节点元素
     *
     * @param beanList 未处理的列表元素
     * @param config   转换配置
     * @param <T>      列表元素类型
     * @return 转换后的树节点元素列表
     */
    private static <T> List<TreeNode<T>> transform(List<T> beanList, BuildTreeConfig<T> config) {
        List<TreeNode<T>> treeNodeList = new ArrayList<>(beanList.size());
        Function<T, Object> idExtractor = config.getIdGetter();
        Function<T, Object> parentIdExtractor = config.getParentIdGetter();
        Function<T, String> labelExtractor = config.getLabelGetter();

        // 转换必要的属性
        for (T each : beanList) {
            TreeNode<T> node = new TreeNode<>();
            node.setId(idExtractor.apply(each));
            node.setParentId(parentIdExtractor.apply(each));
            node.setLabel(labelExtractor.apply(each));
            node.setData(each);
            treeNodeList.add(node);
        }

        return treeNodeList;
    }

    /**
     * 从树节点列表中获取所有根节点
     *
     * @param treeNodeList 树节点列表
     * @param rootId       根节点ID。若为null，则返回所有 parentId 为空的节点
     * @param <T>          树节点内容数据的类型
     * @return 根节点列表
     */
    private static <T> List<TreeNode<T>> getAllRootNode(List<TreeNode<T>> treeNodeList, Object rootId) {
        List<TreeNode<T>> rootList = new ArrayList<>();

        for (TreeNode<T> each : treeNodeList) {
            // 判断是否根节点
            boolean isRoot = (rootId == null && each.getParentId() == null)
                    || (rootId != null && rootId.equals(each.getId()));

            if (isRoot) {
                rootList.add(each);

                // 如果指定了根节点ID，则不需要继续遍历了
                if (rootId != null) {
                    break;
                }
            }
        }

        return rootList;
    }

    /**
     * 建树方法
     *
     * @param beanList 需要用来建树的元素列表
     * @param rootId   根节点ID
     * @param config   建树配置
     * @param <T>      列表中元素类型
     * @return 建好的树
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> beanList, Object rootId, BuildTreeConfig<T> config) {
        List<TreeNode<T>> rootNodeList = new ArrayList<>();

        if (beanList == null || beanList.size() == 0) {
            return rootNodeList;
        }

        List<TreeNode<T>> treeNodeList = transform(beanList, config);
        rootNodeList = getAllRootNode(treeNodeList, rootId);

        // 找不到根节点
        if (rootNodeList.size() == 0) {
            throw new IllegalArgumentException("建树过程中找不到根节点");
        }

        // BFS 初始化
        Queue<TreeNode<T>> queue = new LinkedList<>();
        Set<Object> visitedSet = new HashSet<>(treeNodeList.size());
        for (TreeNode<T> eachRoot : rootNodeList) {
            queue.add(eachRoot);
            visitedSet.add(eachRoot.getId());
        }

        // BFS 建树
        while (!queue.isEmpty()) {
            TreeNode<T> parentNode = queue.poll();

            // 寻找子节点
            for (TreeNode<T> node : treeNodeList) {
                Object parentId = node.getParentId();
                Object currentId = node.getId();

                // 判断是否子节点
                boolean isChild = parentId != null
                        && parentId.equals(parentNode.getId())
                        && !visitedSet.contains(currentId);
                if (isChild) {
                    queue.add(node);
                    visitedSet.add(node.getId());
                    parentNode.getChildren().add(node);
                }
            }
        }
        return rootNodeList;
    }

    public static <T> void bfsWalker(TreeNode<T> root, TreeWalkerPredicate<T> predicate) {
        Queue<TreeNode<T>> aQueue = new LinkedList<>();
        Queue<TreeNode<T>> bQueue;
        Set<Object> visitedSet = new HashSet<>();
        aQueue.add(root);

        int deep = 1;
        visitedSet.add(root);
        predicate.visit(root, null, deep);

        while (!aQueue.isEmpty()) {
            bQueue = aQueue;
            aQueue = new LinkedList<>();
            deep++;

            while (!bQueue.isEmpty()) {
                TreeNode<T> parent = bQueue.poll();

                for (TreeNode<T> child : parent.getChildren()) {
                    if (!visitedSet.contains(child)) {
                        visitedSet.add(child);
                        predicate.visit(child, parent, deep);
                        aQueue.add(child);
                    }
                }
            }
        }
    }

    public static <T> int getDeep(TreeNode<T> root) {
        final int[] maxDeep = {0};
        bfsWalker(root, ((node, parent, deep) -> {
            maxDeep[0] = Math.max(maxDeep[0], deep);
        }));
        return maxDeep[0];
    }

    public static <T> void dfsWalker(TreeNode<T> root, TreeWalkerPredicate<T> predicate) {
        Stack<Map.Entry<TreeNode<T>, Iterator<TreeNode<T>>>> stack = new Stack<>();
        int deep = 1;
        predicate.visit(root, null, deep);
        Iterator<TreeNode<T>> iterator = root.getChildren().iterator();
        Map.Entry<TreeNode<T>, Iterator<TreeNode<T>>> entry = new AbstractMap.SimpleEntry<>(root, iterator);
        stack.push(entry);

        while (!stack.isEmpty()) {
            deep = stack.size() + 1;
            entry = stack.pop();
            TreeNode<T> parent = entry.getKey();
            iterator = entry.getValue();
            if (iterator.hasNext()) {
                TreeNode<T> node = iterator.next();
                predicate.visit(node, parent, deep);

                stack.push(entry);

                if (!CollectionUtils.isEmpty(node.getChildren())) {
                    iterator = node.getChildren().iterator();
                    entry = new AbstractMap.SimpleEntry<>(node, iterator);
                    stack.push(entry);
                }
            }
        }
    }

    @FunctionalInterface
    public interface TreeWalkerPredicate<T> {
        void visit(TreeNode<T> node, TreeNode<T> parent, int deep);
    }
}
