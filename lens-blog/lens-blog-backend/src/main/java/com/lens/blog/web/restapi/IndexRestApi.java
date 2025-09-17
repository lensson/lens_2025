package com.lens.blog.web.restapi;

import com.lens.blog.entity.Link;
import com.lens.blog.entity.Tag;
import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.web.annotion.requestLimit.RequestLimit;
import com.lens.blog.web.constant.RedisConstants;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.service.*;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.enums.EBehavior;
import com.lens.common.base.utils.JsonUtils;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.redis.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 首页 RestApi
 *
 * @author 陌溪
 * @since 2018-09-04
 */
@RestController
@RequestMapping("/index")
@io.swagger.v3.oas.annotations.tags.Tag(name = "首页相关接口", description = "首页相关接口")
@Slf4j
public class IndexRestApi {

    @Autowired
    private TagService tagService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebNavbarService webNavbarService;
    @Autowired
    private RedisUtil redisUtil;

    @RequestLimit(amount = 200, time = 60000)
    @Operation(summary = "通过推荐等级获取博客列表", description = "通过推荐等级获取博客列表")
    @GetMapping("/getBlogByLevel")
    public String getBlogByLevel(HttpServletRequest request,
                                 @Parameter(name = "level", description = "推荐等级", required = false) @RequestParam(name = "level", required = false, defaultValue = "0") Integer level,
                                 @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                 @Parameter(name = "useSort", description = "使用排序", required = false) @RequestParam(name = "useSort", required = false, defaultValue = "0") Integer useSort) {

        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogPageByLevel(level, currentPage, useSort));
    }

    @Operation(summary = "获取首页排行博客", description = "获取首页排行博客")
    @GetMapping("/getHotBlog")
    public String getHotBlog() {

        log.info("获取首页排行博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getHotBlog());
    }

    @Operation(summary = "获取首页最新的博客", description = "获取首页最新的博客")
    @GetMapping("/getNewBlog")
    public String getNewBlog(HttpServletRequest request,
                             @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                             @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getNewBlog(currentPage, null));
    }

    @Operation(summary = "mogu-search调用获取博客的接口[包含内容]", description = "mogu-search调用获取博客的接口")
    @GetMapping("/getBlogBySearch")
    public String getBlogBySearch(HttpServletRequest request,
                                  @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogBySearch(currentPage, null));
    }


    @Operation(summary = "按时间戳获取博客", description = "按时间戳获取博客")
    @GetMapping("/getBlogByTime")
    public String getBlogByTime(HttpServletRequest request,
                                @Parameter(name = "currentPage", description = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @Parameter(name = "pageSize", description = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConstants.BLOG_NEW_COUNT);
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogByTime(currentPage, Long.valueOf(blogNewCount)));
    }

    @Operation(summary = "获取最热标签", description = "获取最热标签")
    @GetMapping("/getHotTag")
    public String getHotTag() {
        String hotTagCount = sysParamsService.getSysParamsValueByKey(SysConstants.HOT_TAG_COUNT);
        // 从Redis中获取友情链接
        String jsonResult = redisUtil.get(RedisConstants.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConstants.SUCCESS, jsonResult2List);
        }
        List<Tag> tagList = tagService.getHotTag(Integer.valueOf(hotTagCount));
        if (tagList.size() > 0) {
            redisUtil.setEx(RedisConstants.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount, JsonUtils.objectToJson(tagList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConstants.SUCCESS, tagList);
    }

    @Operation(summary = "获取友情链接", description = "获取友情链接")
    @GetMapping("/getLink")
    public String getLink() {
        String friendlyLinkCount = sysParamsService.getSysParamsValueByKey(SysConstants.FRIENDLY_LINK_COUNT);
        // 从Redis中获取友情链接
        String jsonResult = redisUtil.get(RedisConstants.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConstants.SUCCESS, jsonResult2List);
        }
        List<Link> linkList = linkService.getListByPageSize(Integer.valueOf(friendlyLinkCount));
        if (linkList.size() > 0) {
            redisUtil.setEx(RedisConstants.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount, JsonUtils.objectToJson(linkList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConstants.SUCCESS, linkList);
    }

    @BussinessLog(value = "点击友情链接", behavior = EBehavior.FRIENDSHIP_LINK)
    @Operation(summary = "增加友情链接点击数", description = "增加友情链接点击数")
    @GetMapping("/addLinkCount")
    public String addLinkCount(@Parameter(name = "uid", description = "友情链接UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        log.info("点击友链");
        return linkService.addLinkCount(uid);
    }

    @Operation(summary = "获取网站配置", description = "获取友情链接")
    @GetMapping("/getWebConfig")
    public String getWebConfig() {
        log.info("获取网站配置");
        return ResultUtil.result(SysConstants.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @Operation(summary = "获取网站导航栏", description = "获取网站导航栏")
    @GetMapping("/getWebNavbar")
    public String getWebNavbar() {
        log.info("获取网站导航栏");
        return ResultUtil.result(SysConstants.SUCCESS, webNavbarService.getAllList());
    }

    @BussinessLog(value = "记录访问页面", behavior = EBehavior.VISIT_PAGE)
    @Operation(summary = "记录访问页面", description = "记录访问页面")
    @GetMapping("/recorderVisitPage")
    public String recorderVisitPage(@Parameter(name = "pageName", description = "页面名称", required = false) @RequestParam(name = "pageName", required = true) String pageName) {

        if (StringUtils.isEmpty(pageName)) {
            return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.INSERT_SUCCESS);
    }
}

