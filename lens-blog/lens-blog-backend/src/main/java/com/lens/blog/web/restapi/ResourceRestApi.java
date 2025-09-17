package com.lens.blog.web.restapi;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lens.blog.entity.ResourceSort;
import com.lens.blog.entity.StudyVideo;
import com.lens.blog.web.constant.SQLConstants;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.ResourceSortService;
import com.lens.blog.xo.service.StudyVideoService;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.web.feign.PictureFeignClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 学习教程 RestApi
 *
 * @author 陌溪
 * @date 2018年10月21日上午11:04:11
 */
@RestController
@RequestMapping("/resource")
@Tag(name = "学习教程相关接口", description = "学习教程相关接口")
@Slf4j
public class ResourceRestApi {
    @Autowired
    WebUtil webUtil;
    @Autowired
    private ResourceSortService resourceSortService;
    @Autowired
    private StudyVideoService studyVideoService;
    @Resource
    private PictureFeignClient pictureFeignClient;

    @Operation(summary = "通过分类来获取视频", description = "通过Uid获取博客内容")
    @GetMapping("/getStudyVideoBySort")
    public String getBlogByUid(HttpServletRequest request,
                               @Parameter(name = "resourceSortUid", description = "资源分类UID", required = false) @RequestParam(name = "resourceSortUid", required = false) String resourceSortUid,
                               @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "8") Long pageSize) {

        QueryWrapper<StudyVideo> queryWrapper = new QueryWrapper<>();
        Page<StudyVideo> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConstants.CLICK_COUNT); //按点击数降序排列
        if (!StringUtils.isEmpty(resourceSortUid)) {
            queryWrapper.eq(SQLConstants.RESOURCE_SORT_UID, resourceSortUid);
        }
        IPage<StudyVideo> pageList = studyVideoService.page(page, queryWrapper);
        List<StudyVideo> list = pageList.getRecords();

        //获取所有的分类
        Set<String> resourceSortUids = new HashSet<>();
        String fileIds = "";
        for (StudyVideo item : list) {
            if (StringUtils.isNotEmpty(item.getResourceSortUid())) {
                resourceSortUids.add(item.getResourceSortUid());
            }
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileIds = fileIds + item.getFileUid() + ",";
            }
        }
        //PictureList
        String result = this.pictureFeignClient.getPicture(fileIds, ",");
        List<Map<String, Object>> picList = webUtil.getPictureMap(result);

        //ResourceSort
        Collection<ResourceSort> resourceSortList = resourceSortService.listByIds(resourceSortUids);
        for (StudyVideo item : list) {
            List<String> photoList = new ArrayList<>();
            for (ResourceSort item2 : resourceSortList) {
                if (item.getResourceSortUid().equals(item2.getUid())) {
                    item.setResourceSort(item2);
                    break;
                }
            }

            for (Map<String, Object> map : picList) {
                //因为资源可能有多个图片
                String fileUid = item.getFileUid();
                List<String> fileUids = StringUtils.changeStringToString(fileUid, ",");
                for (String uid : fileUids) {
                    if (map.get("uid").toString().equals(uid)) {
                        photoList.add(map.get("url").toString());
                    }
                }

            }
            item.setPhotoList(photoList);
        }

        log.info("返回结果");
        return ResultUtil.result(SysConstants.SUCCESS, pageList);
    }

}

