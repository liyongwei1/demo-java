package cn.liyongwei.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeNodeUtil {

    /**
     * 根据传入的节点集合创建树
     *
     * @param nodes 节点集合
     * @param <T>   主键类型
     * @return 树的根节点
     */
    public static <T> TreeNode<T> createTree(Collection<? extends TreeNode<T>> nodes) {
        Map<T, ? extends List<? extends TreeNode<T>>> nodesPartitionByParent = nodes.stream().collect(Collectors.groupingBy(TreeNode::parentId));
        TreeNode<T> root = findRoot(nodes);
        createTree(nodesPartitionByParent, root, 0);
        return root;
    }

    private static <T> void createTree(Map<T, ? extends List<? extends TreeNode<T>>> nodesPartitionByParent, TreeNode<T> currentNode, int level) {
        //设置当前节点的层级
        currentNode.level(level);
        //从nodesPartitionByParent中取出当前节点的子节点列表
        List<? extends TreeNode<T>> subNodes = nodesPartitionByParent.get(currentNode.id());
        if (subNodes == null || subNodes.isEmpty()) {
            return;
        }
        //对子节点列表进行排序
        subNodes.sort(Comparator.comparingInt(TreeNode::priority));
        //迭代
        for (TreeNode<T> node : subNodes) {
            createTree(nodesPartitionByParent, node, level + 1);
        }
    }

    /**
     * 寻找根节点
     *
     * @param nodes 节点集合
     * @param <T>   主键类型
     * @return 根节点
     */
    public static <T> TreeNode<T> findRoot(Collection<? extends TreeNode<T>> nodes) {
        return findRoot(toMap(TreeNode::id, nodes));
    }

    /**
     * 寻找根节点
     *
     * @param nodeMap 节点map，以id为key
     * @param <T>     主键类型
     * @return 根节点
     */
    public static <T> TreeNode<T> findRoot(Map<T, ? extends TreeNode<T>> nodeMap) {
        T rootId = null;
        int rootCnt = 0;
        for (TreeNode<T> node : nodeMap.values()) {
            //如果map中不包含当前节点的父节点，则将其视为一个根节点
            if (!nodeMap.containsKey(node.parentId())) {
                rootId = node.id();
                rootCnt++;
            }
        }
        if (rootCnt == 0) {
            throw new RuntimeException("节点构成了一个回环！");
        } else if (rootCnt > 1) {
            throw new RuntimeException("存在多个根节点!");
        }
        return nodeMap.get(rootId);
    }

    /**
     * 将集合转为Map
     *
     * @param key   key的策略，函数式接口
     * @param nodes 节点集合
     * @param <T>   主键类型
     * @return map
     */
    private static <T> Map<T, ? extends TreeNode<T>> toMap(Function<TreeNode<T>, T> key, Collection<? extends TreeNode<T>> nodes) {
        return nodes.stream().collect(Collectors.toMap(key, Function.identity()));
    }

    private TreeNodeUtil() {
    }
}
