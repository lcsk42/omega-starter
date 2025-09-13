package com.lcsk42.starter.database.handler;

import com.lcsk42.starter.database.annotation.CrudApi;

import java.lang.reflect.Method;

/**
 * CRUD API 处理器
 */
public interface CrudApiHandler {

    /**
     * 前置处理
     *
     * @param crudApi      CRUD API 注解
     * @param args         方法参数
     * @param targetMethod 目标方法
     * @param targetClass  目标类
     * @throws Exception 处理异常
     */
    void preHandle(CrudApi crudApi, Object[] args, Method targetMethod, Class<?> targetClass) throws Exception;
}