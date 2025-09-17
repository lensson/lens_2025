package com.lens.blog.web.restapi;


import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.BlogService;
import com.lens.blog.xo.service.BlogSortService;
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
 * 分类RestApi
 *
 * @author 陌溪
 * @date 2019年11月26日18:59:21
 */
@RestController
@RequestMapping("/classify")
@Tag(name = "分类相关接口", description = "分类相关接口")
@Slf4j
public class ClassifyRestApi {

    @Autowired
    BlogService blogService;
    @Autowired
    TagService tagService;
    @Autowired
    BlogSortService blogSortService;

    /**
     * 获取分类的信息
     */
    @Operation(summary = "获取分类的信息", description = "获取分类的信息")
    @GetMapping("/getBlogSortList")
    public String getBlogSortList() {
        log.info("获取分类信息");
        return ResultUtil.result(SysConstants.SUCCESS, blogSortService.getList());
    }

    @BussinessLog(value = "点击分类", behavior = EBehavior.VISIT_CLASSIFY)
    @Operation(summary = "通过blogSortUid获取文章", description = "通过blogSortUid获取文章")
    @GetMapping("/getArticleByBlogSortUid")
    public String getArticleByBlogSortUid(HttpServletRequest request,
                                          @Parameter(name = "blogSortUid", description = "分类UID", required = false) @RequestParam(name = "blogSortUid", required = false) String blogSortUid,
                                          @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                          @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(blogSortUid)) {
            log.info("点击分类,传入BlogSortUid不能为空");
            return ResultUtil.result(SysConstants.ERROR, "传入BlogSortUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getListByBlogSortUid(blogSortUid, currentPage, pageSize));
    }

}

