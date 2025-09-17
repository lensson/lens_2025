package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.entity.SysParams;
import com.lens.blog.vo.SysParamsVO;
import com.lens.blog.xo.dto.SysParamsPageDTO;
import com.lens.blog.xo.service.SysParamsService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.db.mybatis.page.vo.PageVO;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 参数配置 RestApi
 *
 * @author 陌溪
 * @date 2020年7月21日15:57:41
 */
@RestController
@RequestMapping("/sysParams")
@Tag(name ="参数配置相关接口", description = "参数配置相关接口")
@Slf4j
public class SysParamsRestApi {

    @Autowired
    private SysParamsService sysParamsService;

    @AuthorityVerify
    @Operation(summary = "获取参数配置列表", description ="获取参数配置列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SysParamsPageDTO pageDTO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取参数配置列表");

        pageDTO.setOrderByDescColumn("sort,createTime");
        PageVO<SysParams> pageVO = sysParamsService.page(pageDTO);
        return ResultUtil.successWithData(pageVO);
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加参数配置")
    @Operation(summary = "增加参数配置", description ="增加参数配置")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody SysParamsVO sysParamsVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysParamsService.addSysParams(sysParamsVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑参数配置")
    @Operation(summary = "编辑参数配置", description ="编辑参数配置")
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @Validated({Update.class}) @RequestBody SysParamsVO SysParamsVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysParamsService.editSysParams(SysParamsVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除参数配置")
    @Operation(summary = "批量删除参数配置", description ="批量删除参数配置")
    @PostMapping("/deleteBatch")
    public String delete(@RequestBody List<SysParamsVO> SysParamsVoList, BindingResult result) {

        return sysParamsService.deleteBatchSysParams(SysParamsVoList);
    }
}

