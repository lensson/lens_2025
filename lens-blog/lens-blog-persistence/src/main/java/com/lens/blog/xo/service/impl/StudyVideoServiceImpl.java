package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.StudyVideoMapper;
import com.lens.blog.xo.service.ResourceSortService;
import com.lens.blog.xo.service.StudyVideoService;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.blog.vo.StudyVideoVO;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.ResourceSort;
import com.lens.common.db.entity.StudyVideo;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.web.feign.PictureFeignClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * 学习视频表 服务实现类
 *
 * @author 陌溪
 * @date 2018-09-04
 */
@Service
public class StudyVideoServiceImpl extends SuperServiceImpl<StudyVideoMapper, StudyVideo> implements StudyVideoService {

    @Autowired
    private WebUtil webUtil;

    @Autowired
    private StudyVideoService studyVideoService;

    @Autowired
    private ResourceSortService resourceSortService;

    @Resource
    private PictureFeignClient pictureFeignClient;

    @Override
    public IPage<StudyVideo> getPageList(StudyVideoVO studyVideoVO) {
        QueryWrapper<StudyVideo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(studyVideoVO.getKeyword()) && !StringUtils.isEmpty(studyVideoVO.getKeyword().trim())) {
            queryWrapper.like(SQLConstants.NAME, studyVideoVO.getKeyword().trim());
        }

        Page<StudyVideo> page = new Page<>();
        page.setCurrent(studyVideoVO.getCurrentPage());
        page.setSize(studyVideoVO.getPageSize());
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.CREATE_TIME);
        IPage<StudyVideo> pageList = studyVideoService.page(page, queryWrapper);
        List<StudyVideo> list = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConstants.FILE_SEGMENTATION);
            }
        });
        String pictureResult = null;
        Map<String, String> pictureMap = new HashMap<>();
        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), SysConstants.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);
        picList.forEach(item -> {
            pictureMap.put(item.get(SysConstants.UID).toString(), item.get(SysConstants.URL).toString());
        });

        for (StudyVideo item : list) {
            //获取分类资源
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SysConstants.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }

            if (StringUtils.isNotEmpty(item.getResourceSortUid())) {
                ResourceSort resourceSort = resourceSortService.getById(item.getResourceSortUid());
                item.setResourceSort(resourceSort);
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addStudyVideo(StudyVideoVO studyVideoVO) {
        StudyVideo studyVideo = new StudyVideo();
        studyVideo.setName(studyVideoVO.getName());
        studyVideo.setSummary(studyVideoVO.getSummary());
        studyVideo.setContent(studyVideoVO.getContent());
        studyVideo.setFileUid(studyVideoVO.getFileUid());
        studyVideo.setBaiduPath(studyVideoVO.getBaiduPath());
        studyVideo.setResourceSortUid(studyVideoVO.getResourceSortUid());
        studyVideo.setClickCount(SysConstants.ZERO);
        studyVideo.setUpdateTime(new Date());
        studyVideo.insert();
        return ResultUtil.successWithMessage(MessageConstants.INSERT_SUCCESS);
    }

    @Override
    public String editStudyVideo(StudyVideoVO studyVideoVO) {
        StudyVideo studyVideo = studyVideoService.getById(studyVideoVO.getUid());
        studyVideo.setName(studyVideoVO.getName());
        studyVideo.setSummary(studyVideoVO.getSummary());
        studyVideo.setContent(studyVideoVO.getContent());
        studyVideo.setFileUid(studyVideoVO.getFileUid());
        studyVideo.setBaiduPath(studyVideoVO.getBaiduPath());
        studyVideo.setResourceSortUid(studyVideoVO.getResourceSortUid());
        studyVideo.setUpdateTime(new Date());
        studyVideo.updateById();
        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchStudyVideo(List<StudyVideoVO> studyVideoVOList) {
        if (studyVideoVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConstants.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        studyVideoVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<StudyVideo> blogSortList = studyVideoService.listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = studyVideoService.updateBatchById(blogSortList);

        if (save) {
            return ResultUtil.successWithMessage(MessageConstants.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConstants.DELETE_FAIL);
        }
    }
}
