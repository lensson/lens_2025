package com.lens.blog.admin.restapi;


import com.lens.blog.admin.constant.SysConstants;
import com.lens.blog.xo.service.BlogService;
import com.lens.blog.xo.service.CommentService;
import com.lens.blog.xo.service.UserService;
import com.lens.blog.xo.service.WebVisitService;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.enums.EStatus;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页RestApi
 *
 * @author 陌溪
 * @date 2018年10月22日下午3:27:24
 */
@RestController
@RequestMapping("/index")
@Tag(name ="首页相关接口", description = "首页相关接口")
@Slf4j
public class IndexRestApi {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private WebVisitService webVisitService;
    @Autowired
    private UserService userService;

    @Operation(summary = "首页初始化数据", description ="首页初始化数据")
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {
        Map<String, Object> map = new HashMap<>(Constants.NUM_FOUR);
        map.put(SysConstants.BLOG_COUNT, blogService.getBlogCount(EStatus.ENABLE));
        map.put(SysConstants.COMMENT_COUNT, commentService.getCommentCount(EStatus.ENABLE));
        map.put(SysConstants.USER_COUNT, userService.getUserCount(EStatus.ENABLE));
        map.put(SysConstants.VISIT_COUNT, webVisitService.getWebVisitCount());
        return ResultUtil.result(SysConstants.SUCCESS, map);
    }

    @Operation(summary = "获取最近一周用户独立IP数和访问量", description ="获取最近一周用户独立IP数和访问量")
    @RequestMapping(value = "/getVisitByWeek", method = RequestMethod.GET)
    public String getVisitByWeek() {
        Map<String, Object> visitByWeek = webVisitService.getVisitByWeek();
        return ResultUtil.result(SysConstants.SUCCESS, visitByWeek);
    }

    @Operation(summary = "获取每个标签下文章数目", description ="获取每个标签下文章数目")
    @RequestMapping(value = "/getBlogCountByTag", method = RequestMethod.GET)
    public String getBlogCountByTag() {
        List<Map<String, Object>> blogCountByTag = blogService.getBlogCountByTag();
        return ResultUtil.result(SysConstants.SUCCESS, blogCountByTag);
    }

    @Operation(summary = "获取每个分类下文章数目", description ="获取每个分类下文章数目")
    @RequestMapping(value = "/getBlogCountByBlogSort", method = RequestMethod.GET)
    public String getBlogCountByBlogSort() {

        List<Map<String, Object>> blogCountByTag = blogService.getBlogCountByBlogSort();
        return ResultUtil.result(SysConstants.SUCCESS, blogCountByTag);
    }

    @Operation(summary = "获取一年内的文章贡献数", description ="获取一年内的文章贡献度")
    @RequestMapping(value = "/getBlogContributeCount", method = RequestMethod.GET)
    public String getBlogContributeCount() {

        Map<String, Object> resultMap = blogService.getBlogContributeCount();
        return ResultUtil.result(SysConstants.SUCCESS, resultMap);
    }


}
