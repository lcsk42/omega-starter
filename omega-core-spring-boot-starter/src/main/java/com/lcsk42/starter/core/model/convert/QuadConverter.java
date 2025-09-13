package com.lcsk42.starter.core.model.convert;

import java.util.List;

/**
 * 四种类型 (P, T, V, D) 之间四向转换的接口。
 * 提供常见的日期/时间转换的默认方法。
 *
 * @param <P> PO 类型（持久化对象）
 * @param <T> DTO 类型（数据传输对象）
 * @param <V> VO 类型（视图对象）
 * @param <D> DO 类型（详情对象）
 */
public interface QuadConverter<P, T, V, D> extends BaseConverter {
    /**
     * 将 DTO 类型 (T) 转换为 PO 类型 (P)。
     *
     * @param t DTO 类型的实例
     * @return 转换后的 PO 类型实例
     */
    P toP(T t);

    /**
     * 将 PO 类型 (P) 转换为 VO 类型 (V)。
     *
     * @param p PO 类型的实例
     * @return 转换后的 VO 类型实例
     */
    V toV(P p);

    /**
     * 将 PO 类型 (P) 转换为 DO 类型 (V)。
     *
     * @param p PO 类型的实例
     * @return 转换后的 DO 类型实例
     */
    D toD(P p);

    /**
     * 将 DTO 类型 (T) 的列表转换为 PO 类型 (P)。
     *
     * @param tList DTO 类型实例的列表
     * @return 转换后的 PO 类型实例的列表
     */
    List<P> convertP(List<T> tList);

    /**
     * 将 PO 类型 (P) 的列表转换为 VO 类型 (V)。
     *
     * @param pList PO 类型实例的列表
     * @return 转换后的 VO 类型实例的列表
     */
    List<V> convertV(List<P> pList);

    /**
     * 将 PO 类型 (P) 的列表转换为 DO 类型 (D)。
     *
     * @param pList PO 类型实例的列表
     * @return 转换后的 DO 类型实例的列表
     */
    List<D> convertD(List<P> pList);
}
