package com.lens.blog.xo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.RedisConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.SysParamsMapper;
import com.lens.blog.xo.service.SysParamsService;
import com.lens.blog.vo.SysParamsVO;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.constant.ErrorCode;
import com.lens.common.base.enums.EStatus;
import com.lens.common.base.exception.exceptionType.QueryException;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.SysParams;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * 参数配置 服务实现类
 *
 * @author 陌溪
 * @date 2020年7月21日16:41:28
 */
@Service
public class SysParamsServiceImpl extends SuperServiceImpl<SysParamsMapper, SysParams> implements SysParamsService {

    @Autowired
    SysParamsService sysParamsService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public SysParams getSysParamsByKey(String paramsKey) {
        QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.PARAMS_KEY, paramsKey);
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConstants.LIMIT_ONE);
        SysParams sysParams = sysParamsService.getOne(queryWrapper);
        return sysParams;
    }

    @Override
    public String getSysParamsValueByKey(String paramsKey) {
        // 判断Redis中是否包含该key的数据
        String redisKey = RedisConstants.SYSTEM_PARAMS + RedisConstants.SEGMENTATION + paramsKey;
        String paramsValue = redisUtil.get(redisKey);
        // 如果Redis中不存在，那么从数据库中获取
        if (StringUtils.isEmpty(paramsValue)) {
            SysParams sysParams = sysParamsService.getSysParamsByKey(paramsKey);
            // 如果数据库也不存在，将抛出异常【需要到找到 doc/数据库脚本 更新数据库中的 t_sys_params表】
            if (sysParams == null || StringUtils.isEmpty(sysParams.getParamsValue())) {
                throw new QueryException(ErrorCode.PLEASE_CONFIGURE_SYSTEM_PARAMS, MessageConstants.PLEASE_CONFIGURE_SYSTEM_PARAMS);
            }
            paramsValue = sysParams.getParamsValue();
            redisUtil.set(redisKey, paramsValue);
        }
        return paramsValue;
    }

    @Override
    public String addSysParams(SysParamsVO sysParamsVO) {
        // 判断添加的字典类型是否存在
        QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.PARAMS_KEY, sysParamsVO.getParamsKey());
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConstants.LIMIT_ONE);
        SysParams temp = sysParamsService.getOne(queryWrapper);
        if (temp != null) {
            return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
        }
        SysParams sysParams = new SysParams();
        sysParams.setParamsName(sysParamsVO.getParamsName());
        sysParams.setParamsKey(sysParamsVO.getParamsKey());
        sysParams.setParamsValue(sysParamsVO.getParamsValue());
        sysParams.setParamsType(sysParamsVO.getParamsType());
        sysParams.setRemark(sysParamsVO.getRemark());
        sysParams.setSort(sysParamsVO.getSort());
        sysParams.insert();
        return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editSysParams(SysParamsVO sysParamsVO) {
        SysParams sysParams = sysParamsService.getById(sysParamsVO.getUid());
        // 判断编辑的参数键名是否存在
        if (!sysParamsVO.getParamsKey().equals(sysParams.getParamsKey())) {
            QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.PARAMS_KEY, sysParamsVO.getParamsKey());
            queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
            queryWrapper.last(SysConstants.LIMIT_ONE);
            SysParams temp = sysParamsService.getOne(queryWrapper);
            if (temp != null) {
                return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
            }
        }
        sysParams.setParamsName(sysParamsVO.getParamsName());
        sysParams.setParamsKey(sysParamsVO.getParamsKey());
        sysParams.setParamsValue(sysParamsVO.getParamsValue());
        sysParams.setParamsType(sysParamsVO.getParamsType());
        sysParams.setRemark(sysParamsVO.getRemark());
        sysParams.setSort(sysParamsVO.getSort());
        sysParams.setUpdateTime(new Date());
        sysParams.updateById();
        // 清空Redis中存在的配置
        redisUtil.delete(RedisConstants.SYSTEM_PARAMS + RedisConstants.SEGMENTATION + sysParamsVO.getParamsKey());
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSysParams(List<SysParamsVO> sysParamsVOList) {
        List<String> sysParamsUidList = new ArrayList<>();
        sysParamsVOList.forEach(item -> {
            sysParamsUidList.add(item.getUid());
        });
        if (sysParamsUidList.size() >= 0) {
            Collection<SysParams> sysParamsList = sysParamsService.listByIds(sysParamsUidList);
            // 更新完成数据库后，还需要清空Redis中的缓存，因此需要存储键值
            List<String> redisKeys = new ArrayList<>();
            for(SysParams item : sysParamsList) {
                // 判断删除列表中是否含有系统内置参数
                if(item.getParamsType() == Constants.NUM_ONE) {
                    return ResultUtil.errorWithMessage("系统内置参数无法删除");
                }
                item.setStatus(EStatus.DISABLED);
                redisKeys.add(RedisConstants.SYSTEM_PARAMS + RedisConstants.SEGMENTATION + item.getParamsKey());
            }
            sysParamsService.updateBatchById(sysParamsList);
            // 清空Redis中的配置
            redisUtil.delete(redisKeys);
            return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConstants.DELETE_FAIL);
        }
    }
}
