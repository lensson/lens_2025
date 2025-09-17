package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.ResourceSortVO;
import com.lens.blog.xo.service.ResourceSortService;
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
 * 资源分类表 RestApi
 *
 * @author 陌溪
 * @date 2020年1月9日19:23:28
 */
@Tag(name ="资源分类相关接口", description = "资源分类相关接口")
@RestController
@RequestMapping("/resourceSort")
@Slf4j
public class ResourceSortRestApi {

    @Autowired
    private ResourceSortService resourceSortService;

    @AuthorityVerify
    @Operation(summary = "获取资源分类列表", description ="获取资源分类列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ResourceSortVO resourceSortVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("获取资源分类列表:{}", resourceSortVO);
        return ResultUtil.successWithData(resourceSortService.getPageList(resourceSortVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加资源分类")
    @Operation(summary = "增加资源分类", description ="增加资源分类")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ResourceSortVO resourceSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加资源分类:{}", resourceSortVO);
        return resourceSortService.addResourceSort(resourceSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑资源分类")
    @Operation(summary = "编辑资源分类", description ="编辑资源分类")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody ResourceSortVO resourceSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑资源分类:{}", resourceSortVO);
        return resourceSortService.editResourceSort(resourceSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除资源分类")
    @Operation(summary = "批量删除资源分类", description ="批量删除资源分类")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<ResourceSortVO> resourceSortVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除资源分类:{}", resourceSortVOList);
        return resourceSortService.deleteBatchResourceSort(resourceSortVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "置顶资源分类")
    @Operation(summary = "置顶分类", description ="置顶分类")
    @PostMapping("/stick")
    public String stick(@Validated({Delete.class}) @RequestBody ResourceSortVO resourceSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶分类:{}", resourceSortVO);
        return resourceSortService.stickResourceSort(resourceSortVO);
    }
}

