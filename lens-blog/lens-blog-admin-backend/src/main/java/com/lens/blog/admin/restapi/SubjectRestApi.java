package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.SubjectVO;
import com.lens.blog.xo.service.SubjectService;
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
 * 专题表 RestApi
 *
 * @author 陌溪
 * @date 2020年8月23日08:12:54
 */
@Tag(name ="专题相关接口", description = "专题相关接口"
)
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectRestApi {

    @Autowired
    private SubjectService subjectService;

    @AuthorityVerify
    @Operation(summary = "获取专题列表", description ="获取专题列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SubjectVO subjectVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(subjectService.getPageList(subjectVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加专题")
    @Operation(summary = "增加专题", description ="增加专题")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody SubjectVO subjectVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.addSubject(subjectVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑专题")
    @Operation(summary = "编辑专题", description ="编辑专题")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody SubjectVO subjectVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.editSubject(subjectVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除专题")
    @Operation(summary = "批量删除专题", description ="批量删除专题")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<SubjectVO> subjectVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.deleteBatchSubject(subjectVOList);
    }
}

