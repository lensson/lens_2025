package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.xo.service.SysDictTypeService;
import com.lens.blog.vo.SysDictTypeVO;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.Delete;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 字典类型 RestApi
 *
 * @author 陌溪
 * @date 2020年2月15日21:16:31
 */
@RestController
@RequestMapping("/sysDictType")
@Api(value = "字典类型相关接口", tags = {"字典类型相关接口"})
@Slf4j
public class SysDictTypeRestApi {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @AuthorityVerify
    @ApiOperation(value = "获取字典类型列表", notes = "获取字典类型列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取字典类型列表");
        return ResultUtil.successWithData(sysDictTypeService.getPageList(sysDictTypeVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加字典类型")
    @ApiOperation(value = "增加字典类型", notes = "增加字典类型", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.addSysDictType(sysDictTypeVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑字典类型")
    @ApiOperation(value = "编辑字典类型", notes = "编辑字典类型", response = String.class)
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @Validated({Update.class}) @RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.editSysDictType(sysDictTypeVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除字典类型")
    @ApiOperation(value = "批量删除字典类型", notes = "批量删除字典类型", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(HttpServletRequest request, @Validated({Delete.class}) @RequestBody List<SysDictTypeVO> sysDictTypeVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.deleteBatchSysDictType(sysDictTypeVoList);
    }
}

