package com.lens.blog.xo.service.impl;


import com.lens.blog.mapper.CollectMapper;
import com.lens.blog.xo.service.CollectService;
import com.lens.common.db.entity.Collect;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 收藏表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-08
 */
@Service
public class CollectServiceImpl extends SuperServiceImpl<CollectMapper, Collect> implements CollectService {

}
