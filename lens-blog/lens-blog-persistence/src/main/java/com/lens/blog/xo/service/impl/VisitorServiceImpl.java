package com.lens.blog.xo.service.impl;


import com.lens.blog.mapper.VisitorMapper;
import com.lens.blog.xo.service.VisitorService;
import com.lens.common.db.entity.Visitor;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博主表 服务实现类
 * </p>
 *
 * @author 陌溪
 * @since 2018-09-08
 */
@Service
public class VisitorServiceImpl extends SuperServiceImpl<VisitorMapper, Visitor> implements VisitorService {

}
