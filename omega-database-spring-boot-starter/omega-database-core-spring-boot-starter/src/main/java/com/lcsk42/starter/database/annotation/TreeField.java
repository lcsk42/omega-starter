package com.lcsk42.starter.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 树结构字段映射
 *
 * <p>
 * 用于复杂树场景，例如：表格列表
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TreeField {

    /**
     * ID 字段名
     *
     * @return ID 字段名
     */
    String value() default "key";

    /**
     * 父 ID 字段名
     *
     * @return 父 ID 字段名
     */
    String parentIdKey() default "parentId";

    /**
     * 名称字段名
     *
     * @return 名称字段名
     */
    String nameKey() default "title";

    /**
     * 排序字段名
     *
     * @return 排序字段名
     */
    String weightKey() default "sort";

    /**
     * 子列表字段名
     *
     * @return 子列表字段名
     */
    String childrenKey() default "children";

    /**
     * 递归深度（< 0 不限制）
     *
     * @return 递归深度
     */
    int deep() default -1;

    /**
     * 根节点 ID
     *
     * @return 根节点 ID
     */
    long rootId() default 0L;
}
