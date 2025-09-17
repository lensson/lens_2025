package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.UserVO;
import com.lens.blog.xo.service.UserService;
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

/**
 * 用户表 RestApi
 *
 * @author 陌溪
 * @date 2020年1月4日21:29:09
 */
@RestController
@Tag(name ="用户相关接口", description = "用户相关接口")
@RequestMapping("/user")
@Slf4j
public class UserRestApi {

    @Autowired
    private UserService userService;

    @AuthorityVerify
    @Operation(summary = "获取用户列表", description ="获取用户列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取用户列表: {}", userVO);
        return ResultUtil.successWithData(userService.getPageList(userVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "新增用户")
    @Operation(summary = "新增用户", description ="新增用户")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("新增用户: {}", userVO);
        return userService.addUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑用户")
    @Operation(summary = "编辑用户", description ="编辑用户")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody UserVO userVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑用户: {}", userVO);
        return userService.editUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除用户")
    @Operation(summary = "删除用户", description ="删除用户")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除用户: {}", userVO);
        return userService.deleteUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "重置用户密码")
    @Operation(summary = "重置用户密码", description ="重置用户密码")
    @PostMapping("/resetUserPassword")
    public String resetUserPassword(@Validated({Delete.class}) @RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("重置用户密码: {}", userVO);
        return userService.resetUserPassword(userVO);
    }
}