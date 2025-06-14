package com.lens.common.db.mybatis.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lens.common.db.mybatis.mapper.SuperMapper;
import com.lens.common.db.mybatis.page.vo.PageVO;
import com.lens.common.db.mybatis.service.SuperService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * SuperService 实现类（ 泛型：M 是  mapper(dao) 对象，T 是实体 ）
 *
 * @param <T>
 * @author 陌溪
 * @date 2018年9月4日10:38:18
 */

public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

    @Autowired
    private M baseMapper;

    /**
     * 查询分页
     *
     * @param d             实体类参数对接
     * @param selectColumns 查询返回的列
     */
    @Override
    public <D> PageVO<T> page(D d, SFunction<T, ?>... selectColumns) {
        return baseMapper.selectPage(d, selectColumns);
    }

}
