package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.AdminVO;
import com.lens.blog.xo.service.AdminService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员表 RestApi
 *
 * @author 陌溪
 * @date 2018-09-04
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员相关接口", description = "管理员相关接口")
@Slf4j
public class AdminRestApi {

    @Autowired
    private AdminService adminService;

    @AuthorityVerify
    @Operation(summary = "获取管理员列表", description = "获取管理员列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.getList(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "重置用户密码")
    @Operation(summary = "重置用户密码", description = "重置用户密码")
    @PostMapping("/restPwd")
    public String restPwd(@Validated({Update.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.resetPwd(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "新增管理员")
    @Operation(summary = "新增管理员", description = "新增管理员")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.addAdmin(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "更新管理员")
    @Operation(summary = "更新管理员", description ="更新管理员")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.editAdmin(adminVO);
    }


    @AuthorityVerify
    @OperationLogger(value = "批量删除管理员")
    @Operation(summary = "批量删除管理员", description ="批量删除管理员")
    @PostMapping("/delete")
    public String delete(@Parameter(name = "adminUids", description = "管理员uid集合", required = true) @RequestParam(name = "adminUids", required = true) List<String> adminUids) {
        return adminService.deleteBatchAdmin(adminUids);
    }

    @AuthorityVerify
    @Operation(summary = "获取在线管理员列表", description ="获取在线管理员列表")
    @PostMapping(value = "/getOnlineAdminList")
    public String getOnlineAdminList(@Validated({GetList.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.getOnlineAdminList(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "强退用户")
    @Operation(summary = "强退用户", description ="强退用户")
    @PostMapping(value = "/forceLogout")
    public String forceLogout(@Parameter(name = "tokenUidList", description = "tokenList", required = false) @RequestBody List<String> tokenUidList) {
        return adminService.forceLogout(tokenUidList);
    }
}

