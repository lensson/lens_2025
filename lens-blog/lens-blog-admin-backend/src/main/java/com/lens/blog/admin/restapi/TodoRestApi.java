package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.admin.constant.SysConstants;
import com.lens.blog.xo.service.TodoService;
import com.lens.blog.vo.TodoVO;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.*;
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

/**
 * 待办事项表 RestApi
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@RestController
@Api(value = "待办事项相关接口", tags = {"待办事项相关接口"})
@RequestMapping("/todo")
@Slf4j
public class TodoRestApi {

    @Autowired
    private TodoService todoService;

    @AuthorityVerify
    @ApiOperation(value = "获取代办事项列表", notes = "获取代办事项列表", response = String.class)
    @PostMapping("/getList")
    public String getList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("执行获取代办事项列表");
        return ResultUtil.result(SysConstants.SUCCESS, todoService.getPageList(todoVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "增加代办事项")
    @ApiOperation(value = "增加代办事项", notes = "增加代办事项", response = String.class)
    @PostMapping("/add")
    public String add(HttpServletRequest request, @Validated({Insert.class}) @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.addTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑代办事项")
    @ApiOperation(value = "编辑代办事项", notes = "编辑代办事项", response = String.class)
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @Validated({Update.class}) @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.editTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除代办事项")
    @ApiOperation(value = "删除代办事项", notes = "删除代办事项", response = String.class)
    @PostMapping("/delete")
    public String delete(HttpServletRequest request, @Validated({Delete.class}) @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.deleteTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量编辑代办事项")
    @ApiOperation(value = "批量编辑代办事项", notes = "批量编辑代办事项", response = String.class)
    @PostMapping("/toggleAll")
    public String toggleAll(HttpServletRequest request, @Validated({GetOne.class}) @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.editBatchTodo(todoVO);
    }


}

