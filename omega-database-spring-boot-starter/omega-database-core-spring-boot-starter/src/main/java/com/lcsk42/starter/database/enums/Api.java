package com.lcsk42.starter.database.enums;

/**
 * API 类型枚举
 */
public enum Api {

    /**
     * 分页
     */
    PAGE,

    /**
     * 列表
     */
    LIST,

    /**
     * 树列表
     */
    TREE,

    /**
     * 详情
     */
    GET,

    /**
     * 创建
     */
    CREATE,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 批量删除
     */
    BATCH_DELETE,

    /**
     * 字典列表（下拉选项等场景）
     */
    DICT,

    /**
     * 树型字典列表（树型结构下拉选项等场景）
     */
    TREE_DICT
}