package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.BlogSortVO;
import com.lens.blog.xo.service.BlogSortService;
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
 * 博客分类表 RestApi
 *
 * @author 陌溪
 * @date 2018年9月24日15:45:18
 */
@RestController
@RequestMapping("/blogSort")
@Tag(name ="博客分类相关接口", description = "博客分类相关接口")
@Slf4j
public class BlogSortRestApi {

    @Autowired
    private BlogSortService blogSortService;

    @AuthorityVerify
    @Operation(summary = "获取博客分类列表", description ="获取博客分类列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取博客分类列表");
        return ResultUtil.successWithData(blogSortService.getPageList(blogSortVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加博客分类")
    @Operation(summary = "增加博客分类", description ="增加博客分类")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加博客分类");
        return blogSortService.addBlogSort(blogSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑博客分类")
    @Operation(summary = "编辑博客分类", description ="编辑博客分类")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑博客分类");
        return blogSortService.editBlogSort(blogSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除博客分类")
    @Operation(summary = "批量删除博客分类", description ="批量删除博客分类")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<BlogSortVO> blogSortVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除博客分类");
        return blogSortService.deleteBatchBlogSort(blogSortVoList);
    }

    @AuthorityVerify
    @Operation(summary = "置顶分类", description ="置顶分类")
    @PostMapping("/stick")
    public String stick(@Validated({Delete.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶分类");
        return blogSortService.stickBlogSort(blogSortVO);

    }

    @AuthorityVerify
    @OperationLogger(value = "通过点击量排序博客分类")
    @Operation(summary = "通过点击量排序博客分类", description ="通过点击量排序博客分类")
    @PostMapping("/blogSortByClickCount")
    public String blogSortByClickCount() {
        log.info("通过点击量排序博客分类");
        return blogSortService.blogSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return
     */
    @AuthorityVerify
    @OperationLogger(value = "通过引用量排序博客分类")
    @Operation(summary = "通过引用量排序博客分类", description ="通过引用量排序博客分类")
    @PostMapping("/blogSortByCite")
    public String blogSortByCite() {
        log.info("通过引用量排序博客分类");
        return blogSortService.blogSortByCite();
    }
}

