package cn.liyongwei.utils;

import java.util.List;

public interface TreeNode<T> {

    T id();

    T parentId();

    void subNodes(List<? extends TreeNode<T>> nodes);

    default int priority() {
        return Integer.MAX_VALUE;
    }

    default void level(int level) {
    }
}
