package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.constant.SysConstants;
import com.lens.blog.vo.ExceptionLogVO;
import com.lens.blog.vo.SysLogVO;
import com.lens.blog.xo.service.ExceptionLogService;
import com.lens.blog.xo.service.SysLogService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
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

/**
 * 日志记录表 RestApi
 *
 * @author 陌溪
 * @since 2018年9月24日15:45:18
 */
@RestController
@Tag(name ="操作日志相关接口", description = "操作日志相关接口")
@RequestMapping("/log")
@Slf4j
public class LogRestApi {

    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private ExceptionLogService exceptionLogService;

    @AuthorityVerify
    @Operation(summary = "获取操作日志列表", description ="获取操作日志列表")
    @PostMapping(value = "/getLogList")
    public String getLogList(@Validated({GetList.class}) @RequestBody SysLogVO sysLogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConstants.SUCCESS, sysLogService.getPageList(sysLogVO));
    }

    @AuthorityVerify
    @Operation(summary = "获取系统异常列表", description ="获取系统异常列表")
    @PostMapping(value = "/getExceptionList")
    public String getExceptionList(@Validated({GetList.class}) @RequestBody ExceptionLogVO exceptionLogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConstants.SUCCESS, exceptionLogService.getPageList(exceptionLogVO));
    }
}

