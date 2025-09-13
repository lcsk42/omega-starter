package com.lcsk42.starter.database.model.resp.tree;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 通过转换器将你的实体转化为TreeNodeMap节点实体 属性都存在此处,属性有序，可支持排序
 *
 * @param <T> ID类型
 */
@Getter
public class Tree<T> extends LinkedHashMap<String, Object> implements Node<T> {

    private final TreeNodeConfig treeNodeConfig;
    private Tree<T> parent;

    public Tree() {
        this(null);
    }

    /**
     * 构造
     *
     * @param treeNodeConfig TreeNode配置
     */
    public Tree(TreeNodeConfig treeNodeConfig) {
        this.treeNodeConfig = ObjectUtils.defaultIfNull(
                treeNodeConfig, TreeNodeConfig.DEFAULT_CONFIG);
    }

    /**
     * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
     * 此方法只查找此节点及子节点，采用广度优先遍历。
     *
     * @param id ID
     * @return 节点
     * @since 5.2.4
     */
    public Tree<T> getNode(T id) {
        return getNode(this, id);
    }

    /**
     * 获取所有父节点名称列表
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     *
     * @param id                 节点ID
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表
     * @since 5.2.4
     */
    public List<CharSequence> getParentsName(T id, boolean includeCurrentNode) {
        return getParentsName(getNode(id), includeCurrentNode);
    }

    /**
     * 获取所有父节点名称列表
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     *
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表
     * @since 5.2.4
     */
    public List<CharSequence> getParentsName(boolean includeCurrentNode) {
        return getParentsName(this, includeCurrentNode);
    }

    /**
     * 设置父节点
     *
     * @param parent 父节点
     * @return this
     */
    public Tree<T> setParent(Tree<T> parent) {
        this.parent = parent;
        if (null != parent) {
            this.setParentId(parent.getId());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getId() {
        return (T) this.get(treeNodeConfig.getIdKey());
    }

    @Override
    public Tree<T> setId(T id) {
        this.put(treeNodeConfig.getIdKey(), id);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getParentId() {
        return (T) this.get(treeNodeConfig.getParentIdKey());
    }

    @Override
    public Tree<T> setParentId(T parentId) {
        this.put(treeNodeConfig.getParentIdKey(), parentId);
        return this;
    }

    @Override
    public CharSequence getName() {
        return (CharSequence) this.get(treeNodeConfig.getNameKey());
    }

    @Override
    public Tree<T> setName(CharSequence name) {
        this.put(treeNodeConfig.getNameKey(), name);
        return this;
    }

    @Override
    public Comparable<?> getWeight() {
        return (Comparable<?>) this.get(treeNodeConfig.getWeightKey());
    }

    @Override
    public Tree<T> setWeight(Comparable<?> weight) {
        this.put(treeNodeConfig.getWeightKey(), weight);
        return this;
    }

    /**
     * 获取所有子节点
     *
     * @return 所有子节点
     */
    @SuppressWarnings("unchecked")
    public List<Tree<T>> getChildren() {
        return (List<Tree<T>>) this.get(treeNodeConfig.getChildrenKey());
    }

    /**
     * 是否有子节点，无子节点则此为叶子节点
     *
     * @return 是否有子节点
     */
    public boolean hasChild() {
        return CollectionUtils.isNotEmpty(getChildren());
    }

    /**
     * 递归树并处理子树下的节点：
     *
     * @param consumer 节点处理器
     */
    public void walk(Consumer<Tree<T>> consumer) {
        consumer.accept(this);
        final List<Tree<T>> children = getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach((tree) -> tree.walk(consumer));
        }
    }

    /**
     * 递归过滤并生成新的树<br>
     * 通过{@link Predicate}指定的过滤规则，本节点或子节点满足过滤条件，则保留当前节点，否则抛弃节点及其子节点
     *
     * @param predicate 节点过滤规则函数，只需处理本级节点本身即可
     * @return 过滤后的节点，{@code null} 表示不满足过滤要求，丢弃之
     * @see #filter(Predicate)
     */
    public Tree<T> filterNew(Predicate<Tree<T>> predicate) {
        return cloneTree().filter(predicate);
    }

    /**
     * 递归过滤当前树，注意此方法会修改当前树<br>
     * 通过{@link Predicate}指定的过滤规则，本节点或子节点满足过滤条件，则保留当前节点及其所有子节点，否则抛弃节点及其子节点
     *
     * @param predicate 节点过滤规则函数，只需处理本级节点本身即可
     * @return 过滤后的节点，{@code null} 表示不满足过滤要求，丢弃之
     * @see #filterNew(Predicate)
     */
    public Tree<T> filter(Predicate<Tree<T>> predicate) {
        if (predicate.test(this)) {
            // 本节点满足，则包括所有子节点都保留
            return this;
        }

        final List<Tree<T>> children = getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            // 递归过滤子节点
            final List<Tree<T>> filteredChildren = new ArrayList<>(children.size());
            Tree<T> filteredChild;
            for (Tree<T> child : children) {
                filteredChild = child.filter(predicate);
                if (null != filteredChild) {
                    filteredChildren.add(filteredChild);
                }
            }
            if (CollectionUtils.isNotEmpty(filteredChildren)) {
                // 子节点有符合过滤条件的节点，则本节点保留
                return this.setChildren(filteredChildren);
            } else {
                this.setChildren(null);
            }
        }

        // 子节点都不符合过滤条件，检查本节点
        return null;
    }

    /**
     * 设置子节点，设置后会覆盖所有原有子节点
     *
     * @param children 子节点列表，如果为{@code null}表示移除子节点
     * @return this
     */
    public Tree<T> setChildren(List<Tree<T>> children) {
        if (null == children) {
            this.remove(treeNodeConfig.getChildrenKey());
        }
        this.put(treeNodeConfig.getChildrenKey(), children);
        return this;
    }

    /**
     * 增加子节点，同时关联子节点的父节点为当前节点
     *
     * @param children 子节点列表
     * @return this
     * @since 5.6.7
     */
    @SafeVarargs
    public final Tree<T> addChildren(Tree<T>... children) {
        if (ArrayUtils.isNotEmpty(children)) {
            List<Tree<T>> childrenList = this.getChildren();
            if (null == childrenList) {
                childrenList = new ArrayList<>();
                setChildren(childrenList);
            }
            for (Tree<T> child : children) {
                child.setParent(this);
                childrenList.add(child);
            }
        }
        return this;
    }

    /**
     * 扩展属性
     *
     * @param key   键
     * @param value 扩展值
     */
    public void putExtra(String key, Object value) {
        Validate.notEmpty(key, "Key must be not empty !");
        this.put(key, value);
    }

    /**
     * 递归克隆当前节点（即克隆整个树，保留字段值）<br>
     * 注意，此方法只会克隆节点，节点属性如果是引用类型，不会克隆
     *
     * @return 新的节点
     */
    public Tree<T> cloneTree() {
        final Tree<T> result = ObjectUtils.clone(this);
        result.setChildren(cloneChildren(result));
        return result;
    }

    /**
     * 递归复制子节点
     *
     * @param parent 新的父节点
     * @return 新的子节点列表
     */
    private List<Tree<T>> cloneChildren(final Tree<T> parent) {
        final List<Tree<T>> children = getChildren();
        if (null == children) {
            return null;
        }
        final List<Tree<T>> newChildren = new ArrayList<>(children.size());
        children.forEach((t) -> {
            newChildren.add(t.cloneTree().setParent(parent));
        });
        return newChildren;
    }

    /**
     * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
     * 此方法只查找此节点及子节点，采用递归深度优先遍历。
     *
     * @param <T>  ID类型
     * @param node 节点
     * @param id   ID
     * @return 节点
     */
    public static <T> Tree<T> getNode(Tree<T> node, T id) {
        if (Objects.equals(id, node.getId())) {
            return node;
        }

        final List<Tree<T>> children = node.getChildren();
        if (null == children) {
            return null;
        }

        // 查找子节点
        Tree<T> childNode;
        for (Tree<T> child : children) {
            childNode = child.getNode(id);
            if (null != childNode) {
                return childNode;
            }
        }

        // 未找到节点
        return null;
    }

    /**
     * 获取所有父节点名称列表
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     * </p>
     *
     * @param <T>                节点ID类型
     * @param node               节点
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表，node为null返回空List
     * @since 5.2.4
     */
    public static <T> List<CharSequence> getParentsName(Tree<T> node, boolean includeCurrentNode) {
        final List<CharSequence> result = new ArrayList<>();
        if (null == node) {
            return result;
        }

        if (includeCurrentNode) {
            result.add(node.getName());
        }

        Tree<T> parent = node.getParent();
        CharSequence name;
        while (null != parent) {
            name = parent.getName();
            parent = parent.getParent();
            if (null != name || null != parent) {
                result.add(name);
            }
        }
        return result;
    }

    /**
     * 获取所有父节点ID列表
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发部, 技术中心]
     * </p>
     *
     * @param <T>                节点ID类型
     * @param node               节点
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点 ID 列表，node 为 null 返回空 List
     */
    public static <T> List<T> getParentsId(Tree<T> node, boolean includeCurrentNode) {
        final List<T> result = new ArrayList<>();
        if (null == node) {
            return result;
        }

        if (includeCurrentNode) {
            result.add(node.getId());
        }

        Tree<T> parent = node.getParent();
        T id;
        while (null != parent) {
            id = parent.getId();
            parent = parent.getParent();
            if (null != id || null != parent) {
                result.add(id);
            }
        }
        return result;
    }

    /**
     * 创建空 Tree 的节点
     *
     * @param id  节点 ID
     * @param <E> 节点 ID 类型
     * @return {@link Tree}
     */
    public static <E> Tree<E> createEmptyNode(E id) {
        return new Tree<E>().setId(id);
    }

    /**
     * 函数式构建树状结构(无需继承Tree类)
     *
     * @param nodes        需要构建树集合
     * @param rootId       根节点 ID
     * @param idFunc       获取节点 ID 函数
     * @param parentIdFunc 获取节点父 ID 函数
     * @param setChildFunc 设置孩子集合函数
     * @param <T>          节点 ID 类型
     * @param <E>          节点类型
     * @return List
     */
    public static <T, E> List<E> build(List<E> nodes,
                                       T rootId,
                                       Function<E, T> idFunc,
                                       Function<E, T> parentIdFunc,
                                       BiConsumer<E, List<E>> setChildFunc) {
        List<E> rootList = nodes.stream().filter(tree -> parentIdFunc.apply(tree).equals(rootId)).collect(Collectors.toList());
        Set<T> filterOperated = new HashSet<>(rootList.size() + nodes.size());
        // 对每个根节点都封装它的孩子节点
        rootList.forEach(root -> setChildren(root, nodes, filterOperated, idFunc, parentIdFunc, setChildFunc));
        return rootList;
    }

    /**
     * 树构建
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param <E>            ID类型
     * @param list           源数据集合
     * @param rootId         最顶层父id值 一般为 0 之类
     * @param treeNodeConfig 配置
     * @param nodeParser     转换器
     * @return List
     */
    public static <T, E> List<Tree<E>> build(List<T> list,
                                             E rootId,
                                             TreeNodeConfig treeNodeConfig,
                                             NodeParser<T, E> nodeParser) {
        return buildSingle(list, rootId, treeNodeConfig, nodeParser).getChildren();
    }

    /**
     * 构建单root节点树<br>
     * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param <E>            ID类型
     * @param list           源数据集合
     * @param rootId         最顶层父id值 一般为 0 之类
     * @param treeNodeConfig 配置
     * @param nodeParser     转换器
     * @return {@link Tree}
     */
    public static <T, E> Tree<E> buildSingle(List<T> list, E rootId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
        return TreeBuilder.of(rootId, treeNodeConfig)
                .append(list, nodeParser).build();
    }

    /**
     * 构建多根节点的树结构（支持多个顶级节点）
     *
     * @param <T>            原始数据类型（如实体类、DTO 等）
     * @param <K>            节点 ID 类型（如 Long、String）
     * @param list           原始数据列表
     * @param getId          获取节点 ID 的方法引用，例如：node -> node.getId()
     * @param getParentId    获取节点父级 ID 的方法引用，例如：node -> node.getParentId()
     * @param treeNodeConfig 树节点配置
     * @param parser         树节点属性映射器，用于将原始节点 T 转为 Tree 节点
     * @return 构建完成的树形结构（可能包含多个顶级根节点）
     */
    public static <T, K> List<Tree<K>> buildMultiRoot(List<T> list,
                                                      Function<T, K> getId,
                                                      Function<T, K> getParentId,
                                                      TreeNodeConfig treeNodeConfig,
                                                      NodeParser<T, K> parser) {
        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }

        Set<K> rootParentIds = list.stream()
                .map(getParentId)
                .collect(Collectors.toSet());

        Set<K> idSet = list.stream()
                .map(getId)
                .collect(Collectors.toSet());

        rootParentIds.removeAll(idSet);

        // 构建每一个根 parentId 下的树，并合并成最终结果列表
        return rootParentIds.stream()
                .flatMap(rootParentId -> Tree.build(list, rootParentId, treeNodeConfig, parser).stream())
                .collect(Collectors.toList());
    }

    /**
     * 封装孩子节点
     *
     * @param root           根节点
     * @param nodes          节点集合
     * @param filterOperated 过滤操作Map
     * @param idFunc         获取节点ID函数
     * @param parentIdFunc   获取节点父ID函数
     * @param setChildFunc   设置孩子集合函数
     * @param <T>            节点ID类型
     * @param <E>            节点类型
     */
    private static <T, E> void setChildren(E root, List<E> nodes, Set<T> filterOperated, Function<E, T> idFunc, Function<E, T> parentIdFunc, BiConsumer<E, List<E>> setChildFunc) {
        List<E> children = new ArrayList<>();
        nodes.stream()
                // 过滤出未操作过的节点
                .filter(body -> !filterOperated.contains(idFunc.apply(body)))
                // 过滤出孩子节点
                .filter(body -> Objects.equals(idFunc.apply(root), parentIdFunc.apply(body)))
                .forEach(body -> {
                    filterOperated.add(idFunc.apply(body));
                    children.add(body);
                    // 递归 对每个孩子节点执行同样操作
                    setChildren(body, nodes, filterOperated, idFunc, parentIdFunc, setChildFunc);
                });
        setChildFunc.accept(root, children);
    }
}
