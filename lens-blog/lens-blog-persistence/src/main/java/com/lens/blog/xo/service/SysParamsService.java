package com.lens.blog.xo.service;



import com.lens.blog.vo.SysParamsVO;
import com.lens.common.db.entity.SysParams;
import com.lens.common.db.mybatis.service.SuperService;

import java.util.List;

/**
 * 参数配置 服务类
 *
 * @author 陌溪
 * @date 2020年7月21日15:54:00
 */
public interface SysParamsService extends SuperService<SysParams> {

    /**
     * 通过 参数键名 获取参数配置
     *
     * @param paramsKey
     * @return
     */
    public SysParams getSysParamsByKey(String paramsKey);

    /**
     * 通过 参数键名 获取参数值
     *
     * @param paramsKey
     * @return
     */
    public String getSysParamsValueByKey(String paramsKey);

    /**
     * 新增参数配置
     *
     * @param sysParamsVO
     */
    public String addSysParams(SysParamsVO sysParamsVO);

    /**
     * 编辑参数配置
     *
     * @param sysParamsVO
     */
    public String editSysParams(SysParamsVO sysParamsVO);

    /**
     * 批量删除参数配置
     *
     * @param sysParamsVOList
     */
    public String deleteBatchSysParams(List<SysParamsVO> sysParamsVOList);
}
