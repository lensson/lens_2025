package com.lens.blog.web.restapi;


import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.BlogService;
import com.lens.blog.xo.service.TagService;
import com.lens.common.base.enums.EBehavior;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 标签RestApi
 *
 * @author 陌溪
 * @date 2020年4月26日12:03:17
 */
@RestController
@RequestMapping("/tag")
@Tag(name = "博客标签相关接口", description = "博客标签相关接口")
@Slf4j
public class TagRestApi {

    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;

    /**
     * 获取标签的信息
     *
     * @return
     */
    @Operation(summary = "获取标签的信息", description = "获取标签的信息")
    @GetMapping("/getTagList")
    public String getTagList() {
        log.info("获取标签信息");
        return ResultUtil.result(SysConstants.SUCCESS, tagService.getList());
    }

    /**
     * 通过TagUid获取文章
     *
     * @param request
     * @param currentPage
     * @param pageSize
     * @return
     */
    @BussinessLog(value = "点击标签", behavior = EBehavior.VISIT_TAG)
    @Operation(summary = "通过TagUid获取文章", description = "通过TagUid获取文章")
    @GetMapping("/getArticleByTagUid")
    public String getArticleByTagUid(HttpServletRequest request,
                                     @Parameter(name = "tagUid", description = "标签UID", required = false) @RequestParam(name = "tagUid", required = false) String tagUid,
                                     @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                     @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConstants.ERROR, "传入TagUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }

}

