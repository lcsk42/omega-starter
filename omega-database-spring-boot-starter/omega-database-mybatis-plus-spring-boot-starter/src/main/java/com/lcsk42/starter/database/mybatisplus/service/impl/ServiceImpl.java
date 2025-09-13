package com.lcsk42.starter.database.mybatisplus.service.impl;

import com.lcsk42.starter.database.mybatisplus.mapper.BaseMapper;
import com.lcsk42.starter.database.mybatisplus.module.po.BasePO;
import com.lcsk42.starter.database.mybatisplus.service.IService;

public class ServiceImpl<M extends BaseMapper<P>, P extends BasePO>
        extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, P>
        implements IService<P> {
}