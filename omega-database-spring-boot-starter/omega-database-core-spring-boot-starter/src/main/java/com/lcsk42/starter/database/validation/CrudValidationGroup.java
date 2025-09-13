package com.lcsk42.starter.database.validation;

import jakarta.validation.groups.Default;

/**
 * CRUD 分组校验
 */
public interface CrudValidationGroup extends Default {
    /**
     * CRUD 分组校验-创建
     */
    interface Create extends CrudValidationGroup {
    }

    /**
     * CRUD 分组校验-修改
     */
    interface Update extends CrudValidationGroup {
    }
}
