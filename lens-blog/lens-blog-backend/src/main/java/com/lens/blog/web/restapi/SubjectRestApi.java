package com.lens.blog.web.restapi;

import com.lens.blog.web.constant.SysConstants;

import com.lens.blog.xo.service.SubjectItemService;
import com.lens.blog.xo.service.SubjectService;
import com.lens.blog.vo.SubjectItemVO;
import com.lens.blog.vo.SubjectVO;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
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

/**
 * 专题表 RestApi
 *
 * @author 陌溪
 * @since 2020年8月24日10:37:39
 */
@Api(value = "专题相关接口", tags = {"专题相关接口"})
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectRestApi {

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectItemService subjectItemService;

    @ApiOperation(value = "获取专题列表", notes = "获取专题列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SubjectVO subjectVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConstants.SUCCESS, subjectService.getPageList(subjectVO));
    }

    @ApiOperation(value = "获取专题Item列表", notes = "获取专题Item列表", response = String.class)
    @PostMapping("/getItemList")
    public String getItemList(@Validated({GetList.class}) @RequestBody SubjectItemVO subjectItemVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConstants.SUCCESS, subjectItemService.getPageList(subjectItemVO));
    }

}

