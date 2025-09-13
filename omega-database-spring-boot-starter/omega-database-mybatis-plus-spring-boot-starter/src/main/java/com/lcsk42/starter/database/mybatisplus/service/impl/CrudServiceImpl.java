package com.lcsk42.starter.database.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lcsk42.starter.core.ApplicationContextHolder;
import com.lcsk42.starter.core.constant.StringConstants;
import com.lcsk42.starter.core.model.convert.QuadConverter;
import com.lcsk42.starter.database.annotation.DictModel;
import com.lcsk42.starter.database.annotation.TreeField;
import com.lcsk42.starter.database.model.query.PageQuery;
import com.lcsk42.starter.database.model.query.SortQuery;
import com.lcsk42.starter.database.model.resp.BasePageResp;
import com.lcsk42.starter.database.model.resp.LabelValueResp;
import com.lcsk42.starter.database.model.resp.tree.Tree;
import com.lcsk42.starter.database.model.resp.tree.TreeNodeConfig;
import com.lcsk42.starter.database.mybatisplus.config.DatabaseExtensionProperties;
import com.lcsk42.starter.database.mybatisplus.mapper.BaseMapper;
import com.lcsk42.starter.database.mybatisplus.module.page.PageResp;
import com.lcsk42.starter.database.mybatisplus.module.po.BasePO;
import com.lcsk42.starter.database.mybatisplus.util.QueryWrapperHelper;
import com.lcsk42.starter.database.service.CrudService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CRUD 业务实现基类
 *
 * @param <M> Mapper 接口
 * @param <P> 实体类型
 * @param <V> 列表类型
 * @param <D> 详情类型
 * @param <Q> 查询条件类型
 * @param <C> 创建或修改参数类型
 */
@RequiredArgsConstructor
public class CrudServiceImpl<M extends BaseMapper<P>, P extends BasePO, V, D, Q, C, MS extends QuadConverter<P, C, V, D>>
        extends ServiceImpl<M, P>
        implements CrudService<V, D, Q, C> {

    private final Class<P> persistentClass;
    private final Class<V> viewClass;
    private final Class<D> detailClass;
    private final Class<Q> queryClass;

    private final MS mapStructConverter;

    private List<Field> persistentFields;
    private List<Field> queryFields;


    @Override
    public BasePageResp<V> page(Q query, PageQuery pageQuery) {
        QueryWrapper<P> queryWrapper = this.buildQueryWrapper(query);
        this.sort(queryWrapper, pageQuery);
        IPage<P> page = baseMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        return PageResp.of(page, mapStructConverter::toV);
    }

    @Override
    public List<V> list(Q query, SortQuery sortQuery) {
        return this.list(query, sortQuery, mapStructConverter::convertV);
    }

    @Override
    public List<Tree<Long>> tree(Q query, SortQuery sortQuery, boolean isSimple) {
        return this.tree(query, sortQuery, isSimple, false);
    }

    @Override
    public List<Tree<Long>> tree(Q query, SortQuery sortQuery, boolean isSimple, boolean isSingleRoot) {
        List<V> list = this.list(query, sortQuery);
        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }

        DatabaseExtensionProperties properties = ApplicationContextHolder.getBean(DatabaseExtensionProperties.class);
        DatabaseExtensionProperties.TreeDictModel treeDictModel = properties.getTreeDictModel();

        TreeField treeField = viewClass.getDeclaredAnnotation(TreeField.class);
        TreeNodeConfig treeNodeConfig;
        Long rootId;
        // 简单树（例如：下拉列表）使用 CrudTreeDictModelProperties 全局树型字典映射配置，复杂树（例如：表格）使用 @TreeField 局部结构配置
        if (isSimple) {
            treeNodeConfig = treeDictModel.genTreeNodeConfig();
            rootId = treeDictModel.getRootId();
        } else {
            treeNodeConfig = treeDictModel.genTreeNodeConfig(treeField);
            rootId = treeField.rootId();
        }
        if (isSingleRoot) {
            // 构建单根节点树
            return Tree.build(list, rootId, treeNodeConfig, (node, tree) -> buildTreeField(isSimple, node, tree, treeField));
        } else {
            Function<V, Long> getId = createMethodReference(viewClass, genGetter(treeField.value()));
            Function<V, Long> getParentId = createMethodReference(viewClass, genGetter(treeField.parentIdKey()));
            // 构建多根节点树
            return Tree.buildMultiRoot(list, getId, getParentId, treeNodeConfig, (node, tree) -> buildTreeField(isSimple, node, tree, treeField));
        }
    }

    @Override
    public D get(Long id) {
        P entity = getById(id);
        return mapStructConverter.toD(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(C req) {
        this.beforeCreate(req);
        P entity = mapStructConverter.toP(req);
        baseMapper.insert(entity);
        this.afterCreate(req, entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(C req, Long id) {
        this.beforeUpdate(req, id);
        P oldEntity = this.getById(id);
        if (Objects.isNull(oldEntity)) {
            return;
        }
        P entity = mapStructConverter.toP(req);
        baseMapper.updateById(entity);
        this.afterUpdate(req, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        this.beforeDelete(ids);
        baseMapper.deleteByIds(ids);
        this.afterDelete(ids);
    }

    @Override
    public void export(Q query, SortQuery sortQuery, HttpServletResponse response) {
        List<D> list = this.list(query, sortQuery, mapStructConverter::convertD);
        // todo: 增加 excel 的处理
    }

    @Override
    public List<LabelValueResp<Object>> dict(Q query, SortQuery sortQuery) {
        DictModel dictModel = persistentClass.getDeclaredAnnotation(DictModel.class);
        Validate.notNull(dictModel, "请添加并配置 @DictModel 字典结构信息");
        List<V> list = this.list(query, sortQuery);

        // 解析映射
        Function<String, String> fieldNameMapper = field ->
                (field.contains(StringConstants.DOT)
                        ? StringUtils.substringAfterLast(field, StringConstants.DOT)
                        : field);
        String labelKey = fieldNameMapper.apply(dictModel.labelKey());
        String valueKey = fieldNameMapper.apply(dictModel.valueKey());
        List<String> extraFieldNames = Arrays.stream(dictModel.extraKeys())
                .map(fieldNameMapper)
                .map(QueryWrapperHelper::toUnderlineCase)
                .toList();
        return list.stream()
                .map(entity -> {
                    LabelValueResp<Object> resp = new LabelValueResp<>();
                    resp.setLabel(invokeGetter(entity, QueryWrapperHelper.toUnderlineCase(labelKey)));
                    resp.setValue(invokeGetter(entity, QueryWrapperHelper.toUnderlineCase(valueKey)));
                    if (!extraFieldNames.isEmpty()) {
                        Map<String, Object> extraMap = extraFieldNames.stream()
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        field -> invokeGetter(entity, field)
                                ));
                        resp.setExtra(extraMap);
                    }
                    return resp;
                })
                .toList();
    }

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param convert   类型转换器
     * @return 列表信息
     */
    protected <E> List<E> list(Q query, SortQuery sortQuery, Function<List<P>, List<E>> convert) {
        QueryWrapper<P> queryWrapper = this.buildQueryWrapper(query);
        // 设置排序
        this.sort(queryWrapper, sortQuery);
        List<P> entityList = baseMapper.selectList(queryWrapper);
        return convert.apply(entityList);
    }

    /**
     * 设置排序
     *
     * @param queryWrapper 查询条件封装对象
     * @param sortQuery    排序查询条件
     */
    protected void sort(QueryWrapper<P> queryWrapper, SortQuery sortQuery) {
        if (sortQuery == null || sortQuery.getSort().isUnsorted()) {
            return;
        }
        Sort sort = sortQuery.getSort();
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            String checkProperty;
            // 携带表别名则获取 . 后面的字段名
            if (property.contains(StringConstants.DOT)) {
                checkProperty = StringUtils.substringAfterLast(property, StringConstants.DOT);
            } else {
                checkProperty = property;
            }
            Optional<Field> optional = this.persistentFields.stream()
                    .filter(field -> checkProperty.equals(field.getName()))
                    .findFirst();
            Validate.isTrue(optional.isPresent(), "无效的排序字段 [{}]", property);
            queryWrapper.orderBy(true, order.isAscending(), QueryWrapperHelper.toUnderlineCase(property));
        }
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @return QueryWrapper
     */
    protected QueryWrapper<P> buildQueryWrapper(Q query) {
        QueryWrapper<P> queryWrapper = new QueryWrapper<>();
        // 解析并拼接查询条件
        return QueryWrapperHelper.build(query, this.getQueryFields(), queryWrapper);
    }

    /**
     * 获取当前实体类型字段
     *
     * @return 当前实体类型字段列表
     */
    public List<Field> getEntityFields() {
        if (this.persistentClass == null) {
            this.persistentFields = FieldUtils.getAllFieldsList(this.getEntityClass());
        }
        return this.persistentFields;
    }

    /**
     * 获取当前查询条件类型字段
     *
     * @return 当前查询条件类型字段列表
     */
    public List<Field> getQueryFields() {
        if (this.queryFields == null) {
            this.queryFields = FieldUtils.getAllFieldsList(queryClass);
        }
        return queryFields;
    }

    /**
     * 新增前置处理
     *
     * @param req 创建信息
     */
    protected void beforeCreate(C req) {
        /* 新增前置处理 */
    }

    /**
     * 修改前置处理
     *
     * @param req 修改信息
     * @param id  ID
     */
    protected void beforeUpdate(C req, Long id) {
        /* 修改前置处理 */
    }

    /**
     * 删除前置处理
     *
     * @param ids ID 列表
     */
    protected void beforeDelete(List<Long> ids) {
        /* 删除前置处理 */
    }

    /**
     * 新增后置处理
     *
     * @param req    创建信息
     * @param entity 实体信息
     */
    protected void afterCreate(C req, P entity) {
        /* 新增后置处理 */
    }

    /**
     * 修改后置处理
     *
     * @param req    修改信息
     * @param entity 实体信息
     */
    protected void afterUpdate(C req, P entity) {
        /* 修改后置处理 */
    }

    /**
     * 删除后置处理
     *
     * @param ids ID 列表
     */
    protected void afterDelete(List<Long> ids) {
        /* 删除后置处理 */
    }

    /**
     * 构建树字段
     *
     * @param isSimple  是否简单树结构
     * @param node      节点
     * @param tree      树
     * @param treeField 树字段
     */
    private void buildTreeField(boolean isSimple, V node, Tree<Long> tree, TreeField treeField) {
        tree.setId(invokeGetter(node, treeField.value()));
        tree.setParentId(invokeGetter(node, treeField.parentIdKey()));
        tree.setName(invokeGetter(node, treeField.nameKey()));
        tree.setWeight(invokeGetter(node, treeField.weightKey()));

        // 如果构建简单树结构，则不包含扩展字段
        if (!isSimple) {
            FieldUtils.getAllFieldsList(viewClass)
                    .stream()
                    .filter(it ->
                            StringUtils.equalsAnyIgnoreCase(it.getName(),
                                    treeField.value(),
                                    treeField.parentIdKey(),
                                    treeField.nameKey(),
                                    treeField.weightKey(),
                                    treeField.childrenKey()))
                    .forEach(f -> tree.putExtra(f.getName(), invokeGetter(node, f.getName())));
        }
    }
}
