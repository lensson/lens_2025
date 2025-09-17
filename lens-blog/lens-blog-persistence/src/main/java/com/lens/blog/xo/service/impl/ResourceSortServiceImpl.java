package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.entity.ResourceSort;
import com.lens.blog.entity.StudyVideo;
import com.lens.blog.xo.mapper.ResourceSortMapper;
import com.lens.blog.vo.ResourceSortVO;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.xo.service.ResourceSortService;
import com.lens.blog.xo.service.StudyVideoService;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.common.base.constant.BaseSysConstants;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.constant.BaseSQLConstants;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.web.feign.PictureFeignClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 资源分类表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-04
 */
@Service
public class ResourceSortServiceImpl extends SuperServiceImpl<ResourceSortMapper, ResourceSort> implements ResourceSortService {

    @Resource
    private ResourceSortService resourceSortService;

    @Resource
    private StudyVideoService studyVideoService;

    @Resource
    private PictureFeignClient pictureFeignClient;

    @Resource
    private WebUtil webUtil;

    @Override
    public IPage<ResourceSort> getPageList(ResourceSortVO resourceSortVO) {
        QueryWrapper<ResourceSort> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(resourceSortVO.getKeyword()) && !StringUtils.isEmpty(resourceSortVO.getKeyword().trim())) {
            queryWrapper.like(BaseSQLConstants.SORT_NAME, resourceSortVO.getKeyword().trim());
        }
        Page<ResourceSort> page = new Page<>();
        page.setCurrent(resourceSortVO.getCurrentPage());
        page.setSize(resourceSortVO.getPageSize());
        queryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConstants.SORT);
        IPage<ResourceSort> pageList = resourceSortService.page(page, queryWrapper);
        List<ResourceSort> list = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + BaseSysConstants.FILE_SEGMENTATION);
            }
        });
        String pictureResult = null;
        Map<String, String> pictureMap = new HashMap<>();

        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), BaseSysConstants.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);

        picList.forEach(item -> {
            pictureMap.put(item.get(SysConstants.UID).toString(), item.get(SysConstants.URL).toString());
        });

        for (ResourceSort item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), BaseSysConstants.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addResourceSort(ResourceSortVO resourceSortVO) {
        /**
         * 判断需要增加的分类是否存在
         */
        QueryWrapper<ResourceSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConstants.SORT_NAME, resourceSortVO.getSortName());
        queryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
        ResourceSort tempSort = resourceSortService.getOne(queryWrapper);
        if (tempSort != null) {
            return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
        }

        ResourceSort resourceSort = new ResourceSort();
        resourceSort.setSortName(resourceSortVO.getSortName());
        resourceSort.setContent(resourceSortVO.getContent());
        resourceSort.setFileUid(resourceSortVO.getFileUid());
        resourceSort.setSort(resourceSortVO.getSort());
        resourceSort.setStatus(EStatus.ENABLE);
        resourceSort.insert();
        return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editResourceSort(ResourceSortVO resourceSortVO) {

        ResourceSort resourceSort = resourceSortService.getById(resourceSortVO.getUid());
        /**
         * 判断需要编辑的分类是否存在
         */
        if (!resourceSort.getSortName().equals(resourceSortVO.getSortName())) {
            QueryWrapper<ResourceSort> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(BaseSQLConstants.SORT_NAME, resourceSortVO.getSortName());
            queryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
            ResourceSort tempSort = resourceSortService.getOne(queryWrapper);
            if (tempSort != null) {
                return ResultUtil.errorWithMessage(MessageConstants.ENTITY_EXIST);
            }
        }

        resourceSort.setSortName(resourceSortVO.getSortName());
        resourceSort.setContent(resourceSortVO.getContent());
        resourceSort.setFileUid(resourceSortVO.getFileUid());
        resourceSort.setSort(resourceSortVO.getSort());
        resourceSort.setUpdateTime(new Date());
        resourceSort.updateById();
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchResourceSort(List<ResourceSortVO> resourceSortVOList) {
        if (resourceSortVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        resourceSortVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        // 判断要删除的分类，是否有资源
        QueryWrapper<StudyVideo> studyVideoQueryWrapper = new QueryWrapper<>();
        studyVideoQueryWrapper.eq(BaseSQLConstants.STATUS, EStatus.ENABLE);
        studyVideoQueryWrapper.in(BaseSQLConstants.RESOURCE_SORT_UID, uids);
        Long count = studyVideoService.count(studyVideoQueryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage(MessageConstants.RESOURCE_UNDER_THIS_SORT);
        }
        Collection<ResourceSort> resourceSortList = resourceSortService.listByIds(uids);

        resourceSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = resourceSortService.updateBatchById(resourceSortList);

        if (save) {
            return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConstants.DELETE_FAIL);
        }
    }

    @Override
    public String stickResourceSort(ResourceSortVO resourceSortVO) {
        ResourceSort resourceSort = resourceSortService.getById(resourceSortVO.getUid());

        //查找出最大的那一个
        QueryWrapper<ResourceSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(BaseSQLConstants.SORT);
        Page<ResourceSort> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<ResourceSort> pageList = resourceSortService.page(page, queryWrapper);
        List<ResourceSort> list = pageList.getRecords();
        ResourceSort maxSort = list.get(0);

        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        if (maxSort.getUid().equals(resourceSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConstants.THIS_SORT_IS_TOP);
        }

        Integer sortCount = maxSort.getSort() + 1;

        resourceSort.setSort(sortCount);
        resourceSort.setUpdateTime(new Date());
        resourceSort.updateById();

        return ResultUtil.successWithMessage(MessageConstants.OPERATION_SUCCESS);
    }
}
