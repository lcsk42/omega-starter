package com.lcsk42.starter.database.annotation;

import com.lcsk42.starter.database.enums.Api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CRUD（增删改查）API
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrudApi {

    /**
     * API 类型
     */
    Api value() default Api.LIST;
}