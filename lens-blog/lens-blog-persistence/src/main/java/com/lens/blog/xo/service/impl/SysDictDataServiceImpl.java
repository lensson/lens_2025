package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.SysDictDataMapper;
import com.lens.blog.xo.service.SysDictDataService;
import com.lens.blog.xo.service.SysDictTypeService;
import com.lens.blog.vo.SysDictDataVO;
import com.lens.common.base.enums.EPublish;
import com.lens.common.base.enums.EStatus;
import com.lens.common.base.utils.JsonUtils;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.SysDictData;
import com.lens.common.db.entity.SysDictType;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.redis.utils.RedisUtil;
import com.lens.common.web.holder.RequestHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 字典数据 服务实现类
 *
 * @author 陌溪
 * @date 2020年2月15日21:09:15
 */
@Service
public class SysDictDataServiceImpl extends SuperServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Autowired
    private SysDictDataService sysDictDataService;
    @Autowired
    private SysDictTypeService sysDictTypeService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<SysDictData> getPageList(SysDictDataVO sysDictDataVO) {
        QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
        // 字典类型UID
        if (StringUtils.isNotEmpty(sysDictDataVO.getDictTypeUid())) {
            queryWrapper.eq(SQLConstants.DICT_TYPE_UID, sysDictDataVO.getDictTypeUid());
        }
        // 字典标签
        if (StringUtils.isNotEmpty(sysDictDataVO.getDictLabel()) && !StringUtils.isEmpty(sysDictDataVO.getDictLabel().trim())) {
            queryWrapper.like(SQLConstants.DICT_LABEL, sysDictDataVO.getDictLabel().trim());
        }
        Page<SysDictData> page = new Page<>();
        page.setCurrent(sysDictDataVO.getCurrentPage());
        page.setSize(sysDictDataVO.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.SORT, SQLConstants.CREATE_TIME);
        IPage<SysDictData> pageList = sysDictDataService.page(page, queryWrapper);
        List<SysDictData> sysDictDataList = pageList.getRecords();
        Set<String> dictTypeUidList = new HashSet<>();
        sysDictDataList.forEach(item -> {
            dictTypeUidList.add(item.getDictTypeUid());
        });
        Collection<SysDictType> dictTypeList = new ArrayList<>();
        if (dictTypeUidList.size() > 0) {
            dictTypeList = sysDictTypeService.listByIds(dictTypeUidList);
        }
        Map<String, SysDictType> dictTypeMap = new HashMap<>();
        dictTypeList.forEach(item -> {
            dictTypeMap.put(item.getUid(), item);
        });
        sysDictDataList.forEach(item -> {
            item.setSysDictType(dictTypeMap.get(item.getDictTypeUid()));
        });
        pageList.setRecords(sysDictDataList);
        return pageList;
    }

    @Override
    public String addSysDictData(SysDictDataVO sysDictDataVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        String adminUid = request.getAttribute(SysConstants.ADMIN_UID).toString();
        // 判断添加的字典数据是否存在
        QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.DICT_LABEL, sysDictDataVO.getDictLabel());
        queryWrapper.eq(SQLConstants.DICT_TYPE_UID, sysDictDataVO.getDictTypeUid());
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConstants.LIMIT_ONE);
        SysDictData temp = sysDictDataService.getOne(queryWrapper);
        if (temp != null) {
            return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
        }
        SysDictData sysDictData = new SysDictData();
        // 插入字典数据，忽略状态位【使用Spring工具类提供的深拷贝，避免出现大量模板代码】
        BeanUtils.copyProperties(sysDictDataVO, sysDictData, SysConstants.STATUS);
        sysDictData.setCreateByUid(adminUid);
        sysDictData.setUpdateByUid(adminUid);
        sysDictData.insert();
        return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editSysDictData(SysDictDataVO sysDictDataVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        String adminUid = request.getAttribute(SysConstants.ADMIN_UID).toString();
        SysDictData sysDictData = sysDictDataService.getById(sysDictDataVO.getUid());
        // 更改了标签名时，判断更改的字典数据是否存在
        if (!sysDictData.getDictLabel().equals(sysDictDataVO.getDictLabel())) {
            QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConstants.DICT_LABEL, sysDictDataVO.getDictLabel());
            queryWrapper.eq(SQLConstants.DICT_TYPE_UID, sysDictDataVO.getDictTypeUid());
            queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
            queryWrapper.last(SysConstants.LIMIT_ONE);
            SysDictData temp = sysDictDataService.getOne(queryWrapper);
            if (temp != null) {
                return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
            }
        }
        // 更新数据字典【使用Spring工具类提供的深拷贝，避免出现大量模板代码】
        BeanUtils.copyProperties(sysDictDataVO, sysDictData, SysConstants.STATUS, SysConstants.UID);
        sysDictData.setUpdateByUid(adminUid);
        sysDictData.setUpdateTime(new Date());
        sysDictData.setUpdateByUid(adminUid);
        sysDictData.updateById();

        // 获取Redis中特定前缀
        Set<String> keys = redisUtil.keys(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + "*");
        redisUtil.delete(keys);
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSysDictData(List<SysDictDataVO> sysDictDataVOList) {
        HttpServletRequest request = RequestHolder.getRequest();
        String adminUid = request.getAttribute(SysConstants.ADMIN_UID).toString();
        if (sysDictDataVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        sysDictDataVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<SysDictData> sysDictDataList = sysDictDataService.listByIds(uids);
        sysDictDataList.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
            item.setUpdateTime(new Date());
            item.setUpdateByUid(adminUid);
        });
        Boolean save = sysDictDataService.updateBatchById(sysDictDataList);
        // 获取Redis中特定前缀
        Set<String> keys = redisUtil.keys(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + "*");
        redisUtil.delete(keys);
        if (save) {
            return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public Map<String, Object> getListByDictType(String dictType) {
        //从Redis中获取内容
        String jsonResult = redisUtil.get(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + dictType);
        //判断redis中是否有字典
        if (StringUtils.isNotEmpty(jsonResult)) {
            Map<String, Object> map = JsonUtils.jsonToMap(jsonResult);
            return map;
        }
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.DICT_TYPE, dictType);
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.last(SysConstants.LIMIT_ONE);
        SysDictType sysDictType = sysDictTypeService.getOne(queryWrapper);
        if (sysDictType == null) {
            return new HashMap<>();
        }
        QueryWrapper<SysDictData> sysDictDataQueryWrapper = new QueryWrapper<>();
        sysDictDataQueryWrapper.eq(SQLConstants.IS_PUBLISH, EPublish.PUBLISH);
        sysDictDataQueryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        sysDictDataQueryWrapper.eq(SQLConstants.DICT_TYPE_UID, sysDictType.getUid());
        sysDictDataQueryWrapper.orderByDesc(SQLConstants.SORT, SQLConstants.CREATE_TIME);
        List<SysDictData> list = sysDictDataService.list(sysDictDataQueryWrapper);

        String defaultValue = null;
        for (SysDictData sysDictData : list) {
            // 获取默认值
            if (sysDictData.getIsDefault() == SysConstants.ONE) {
                defaultValue = sysDictData.getDictValue();
                break;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put(SysConstants.DEFAULT_VALUE, defaultValue);
        result.put(SysConstants.LIST, list);
        redisUtil.setEx(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + dictType, JsonUtils.objectToJson(result).toString(), 1, TimeUnit.DAYS);
        return result;
    }

    @Override
    public Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<String> tempTypeList = new ArrayList<>();
        dictTypeList.forEach(item -> {
            //从Redis中获取内容
            String jsonResult = redisUtil.get(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + item);
            //判断redis中是否有字典
            if (StringUtils.isNotEmpty(jsonResult)) {
                Map<String, Object> tempMap = JsonUtils.jsonToMap(jsonResult);
                map.put(item, tempMap);
            } else {
                // 如果redis中没有该字典，那么从数据库中查询
                tempTypeList.add(item);
            }
        });
        // 表示数据全部从redis中获取到了，直接返回即可
        if (tempTypeList.size() <= 0) {
            return map;
        }
        // 查询 dict_type 在 tempTypeList中的
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConstants.DICT_TYPE, tempTypeList);
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConstants.IS_PUBLISH, EPublish.PUBLISH);
        List<SysDictType> sysDictTypeList = sysDictTypeService.list(queryWrapper);
        sysDictTypeList.forEach(item -> {
            QueryWrapper<SysDictData> sysDictDataQueryWrapper = new QueryWrapper<>();
            sysDictDataQueryWrapper.eq(SQLConstants.IS_PUBLISH, EPublish.PUBLISH);
            sysDictDataQueryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
            sysDictDataQueryWrapper.eq(SQLConstants.DICT_TYPE_UID, item.getUid());
            sysDictDataQueryWrapper.orderByDesc(SQLConstants.SORT, SQLConstants.CREATE_TIME);
            List<SysDictData> list = sysDictDataService.list(sysDictDataQueryWrapper);
            String defaultValue = null;
            for (SysDictData sysDictData : list) {
                // 获取默认值
                if (sysDictData.getIsDefault() == SysConstants.ONE) {
                    defaultValue = sysDictData.getDictValue();
                    break;
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put(SysConstants.DEFAULT_VALUE, defaultValue);
            result.put(SysConstants.LIST, list);
            map.put(item.getDictType(), result);
            redisUtil.setEx(SysConstants.REDIS_DICT_TYPE + SysConstants.REDIS_SEGMENTATION + item.getDictType(), JsonUtils.objectToJson(result).toString(), 1, TimeUnit.DAYS);
        });
        return map;
    }
}
