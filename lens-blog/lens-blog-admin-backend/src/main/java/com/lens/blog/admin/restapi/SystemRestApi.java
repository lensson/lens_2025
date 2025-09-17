package com.lens.blog.admin.restapi;

import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.AdminVO;
import com.lens.blog.xo.service.AdminService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置RestApi
 *
 * @author 陌溪
 * @date 2018年11月6日下午8:23:36
 */

@RestController
@RequestMapping("/system")
@Tag(name ="系统设置相关接口", description = "系统设置相关接口")
@Slf4j
public class SystemRestApi {

    @Autowired
    private AdminService adminService;

    @AuthorityVerify
    @Operation(summary = "获取我的信息", description ="获取我的信息")
    @GetMapping("/getMe")
    public String getMe() {
        return ResultUtil.successWithData(adminService.getMe());
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑我的信息")
    @Operation(summary = "编辑我的信息", description ="获取我的信息")
    @PostMapping("/editMe")
    public String editMe(@Validated({Update.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.editMe(adminVO);
    }

    @AuthorityVerify
    @Operation(summary = "修改密码", description ="修改密码")
    @PostMapping("/changePwd")
    public String changePwd(@Parameter(name = "oldPwd", description = "旧密码", required = false) @RequestParam(name = "oldPwd", required = false) String oldPwd,
                            @Parameter(name = "newPwd", description = "新密码", required = false) @RequestParam(name = "newPwd", required = false) String newPwd) {
        return adminService.changePwd(oldPwd, newPwd);
    }

}
