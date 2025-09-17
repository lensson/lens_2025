package com.lens.blog.web.restapi;


import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.service.BlogService;
import com.lens.blog.xo.service.SystemConfigService;
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

@RestController
@RequestMapping("/search")
@Tag(name = "SQL搜索相关接口", description = "SQL搜索相关接口")
@Slf4j
public class SearchRestApi {
    @Autowired
    private BlogService blogService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 使用SQL语句搜索博客，如需使用Solr或者ElasticSearch 需要启动 mogu-search
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    @BussinessLog(value = "搜索Blog", behavior = EBehavior.BLOG_SEARCH)
    @Operation(summary = "搜索Blog", description = "搜索Blog")
    @GetMapping("/sqlSearchBlog")
    public String sqlSearchBlog(@Parameter(name = "keywords", description = "关键字", required = true) @RequestParam(required = true) String keywords,
                                @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.KEYWORD_IS_NOT_EMPTY);
        }
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogByKeyword(keywords, currentPage, pageSize));

    }

    @BussinessLog(value = "根据标签获取相关的博客", behavior = EBehavior.BLOG_TAG)
    @Operation(summary = "根据标签获取相关的博客", description = "根据标签获取相关的博客")
    @GetMapping("/searchBlogByTag")
    public String searchBlogByTag(HttpServletRequest request,
                                  @Parameter(name = "tagUid", description = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                  @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConstants.ERROR, "标签不能为空");
        }
        return ResultUtil.result(SysConstants.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }

    @BussinessLog(value = "根据分类获取相关的博客", behavior = EBehavior.BLOG_SORT)
    @Operation(summary = "根据分类获取相关的博客", description = "根据标签获取相关的博客")
    @GetMapping("/searchBlogBySort")
    public String searchBlogBySort(HttpServletRequest request,
                                   @Parameter(name = "blogSortUid", description = "博客分类UID", required = true) @RequestParam(name = "blogSortUid", required = true) String blogSortUid,
                                   @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                   @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            return ResultUtil.result(SysConstants.ERROR, "uid不能为空");
        }
        return ResultUtil.result(SysConstants.SUCCESS, blogService.searchBlogByBlogSort(blogSortUid, currentPage, pageSize));
    }

    @BussinessLog(value = "根据作者获取相关的博客", behavior = EBehavior.BLOG_AUTHOR)
    @Operation(summary = "根据作者获取相关的博客", description = "根据作者获取相关的博客")
    @GetMapping("/searchBlogByAuthor")
    public String searchBlogByAuthor(HttpServletRequest request,
                                     @Parameter(name = "author", description = "作者名称", required = true) @RequestParam(name = "author", required = true) String author,
                                     @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                     @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(author)) {
            return ResultUtil.result(SysConstants.ERROR, "作者不能为空");
        }
        return ResultUtil.result(SysConstants.SUCCESS, blogService.searchBlogByAuthor(author, currentPage, pageSize));
    }

    @Operation(summary = "获取搜索模式", description = "获取搜索模式")
    @GetMapping(value = "/getSearchModel")
    public String getSearchModel() {
        return ResultUtil.successWithData(systemConfigService.getSearchModel());
    }

}
