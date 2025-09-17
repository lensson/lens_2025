package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.StudyVideoVO;
import com.lens.blog.xo.service.StudyVideoService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.Delete;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频表 RestApi
 *
 * @author 陌溪
 * @date 2020年1月10日22:44:35
 */
@RestController
@RequestMapping("/studyVideo")
@Tag(name ="学习视频相关接口", description = "学习视频相关接口")
@Slf4j
public class StudyVideoRestApi {

    @Autowired
    private StudyVideoService studyVideoService;

    @AuthorityVerify
    @Operation(summary = "获取学习视频列表", description ="获取学习视频列表")
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody StudyVideoVO studyVideoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取学习视频列表: {}", studyVideoVO);
        return ResultUtil.successWithData(studyVideoService.getPageList(studyVideoVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加学习视频")
    @Operation(summary = "增加学习视频", description ="增加学习视频")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody StudyVideoVO studyVideoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加学习视频: {}", studyVideoVO);
        return studyVideoService.addStudyVideo(studyVideoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑学习视频")
    @Operation(summary = "编辑学习视频", description ="编辑学习视频")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody StudyVideoVO studyVideoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑学习视频: {}", studyVideoVO);
        return studyVideoService.editStudyVideo(studyVideoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除学习视频")
    @Operation(summary = "删除学习视频", description ="删除学习视频")
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<StudyVideoVO> studyVideoVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除学习视频: {}", studyVideoVOList);
        return studyVideoService.deleteBatchStudyVideo(studyVideoVOList);
    }

}

