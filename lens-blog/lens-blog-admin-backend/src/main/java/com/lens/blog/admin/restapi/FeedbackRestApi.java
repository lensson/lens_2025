package com.lens.blog.admin.restapi;



import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.admin.constant.SysConstants;
import com.lens.blog.xo.service.FeedbackService;
import com.lens.blog.vo.FeedbackVO;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.Delete;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 反馈表 RestApi
 *
 * @author 陌溪
 * @date 2020年3月16日08:38:07
 */
@RestController
@Api(value = "用户反馈相关接口", tags = {"用户反馈相关接口"})
@RequestMapping("/feedback")
@Slf4j
public class FeedbackRestApi {

    @Autowired
    FeedbackService feedbackService;

    @AuthorityVerify
    @ApiOperation(value = "获取反馈列表", notes = "获取反馈列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody FeedbackVO feedbackVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取反馈列表: {}", feedbackVO);
        return ResultUtil.result(SysConstants.SUCCESS, feedbackService.getPageList(feedbackVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑反馈")
    @ApiOperation(value = "编辑反馈", notes = "编辑反馈", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody FeedbackVO feedbackVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑反馈: {}", feedbackVO);
        return feedbackService.addFeedback(feedbackVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除反馈")
    @ApiOperation(value = "批量删除反馈", notes = "批量删除反馈", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<FeedbackVO> feedbackVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除反馈: {}", feedbackVOList);
        return feedbackService.deleteBatchFeedback(feedbackVOList);
    }

}

