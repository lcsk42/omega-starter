package com.lcsk42.starter.database.model.resp.tree;

/**
 * 树节点解析器
 *
 * @param <T> 转换的实体 为数据源里的对象类型
 */
@FunctionalInterface
public interface NodeParser<T, E> {
	/**
	 * @param object   源数据实体
	 * @param treeNode 树节点实体
	 */
	void parse(T object, Tree<E> treeNode);
}