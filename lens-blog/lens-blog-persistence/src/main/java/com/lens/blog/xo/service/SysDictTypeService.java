package com.lens.blog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lens.blog.vo.SysDictTypeVO;
import com.lens.common.db.entity.SysDictType;
import com.lens.common.db.mybatis.service.SuperService;


import java.util.List;

/**
 * 字典类型 服务类
 *
 * @author 陌溪
 * @date 2020年2月15日21:06:45
 */
public interface SysDictTypeService extends SuperService<SysDictType> {
    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeVO
     * @return
     */
    public IPage<SysDictType> getPageList(SysDictTypeVO sysDictTypeVO);

    /**
     * 新增字典类型
     *
     * @param sysDictTypeVO
     */
    public String addSysDictType(SysDictTypeVO sysDictTypeVO);

    /**
     * 编辑字典类型
     *
     * @param sysDictTypeVO
     */
    public String editSysDictType(SysDictTypeVO sysDictTypeVO);

    /**
     * 批量删除字典类型
     *
     * @param sysDictTypeVOList
     */
    public String deleteBatchSysDictType(List<SysDictTypeVO> sysDictTypeVOList);
}
