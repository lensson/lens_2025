package com.lens.blog.web.restapi;



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
import com.lens.common.db.entity.Link;
import com.lens.common.db.entity.Tag;
import com.lens.common.redis.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "首页相关接口", tags = {"首页相关接口"})
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
    @ApiOperation(value = "通过推荐等级获取博客列表", notes = "通过推荐等级获取博客列表")
    @GetMapping("/getBlogByLevel")
    public String getBlogByLevel(HttpServletRequest request,
                                 @ApiParam(name = "level", value = "推荐等级", required = false) @RequestParam(name = "level", required = false, defaultValue = "0") Integer level,
                                 @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                 @ApiParam(name = "useSort", value = "使用排序", required = false) @RequestParam(name = "useSort", required = false, defaultValue = "0") Integer useSort) {

        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogPageByLevel(level, currentPage, useSort));
    }

    @ApiOperation(value = "获取首页排行博客", notes = "获取首页排行博客")
    @GetMapping("/getHotBlog")
    public String getHotBlog() {

        log.info("获取首页排行博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getHotBlog());
    }

    @ApiOperation(value = "获取首页最新的博客", notes = "获取首页最新的博客")
    @GetMapping("/getNewBlog")
    public String getNewBlog(HttpServletRequest request,
                             @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                             @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getNewBlog(currentPage, null));
    }

    @ApiOperation(value = "mogu-search调用获取博客的接口[包含内容]", notes = "mogu-search调用获取博客的接口")
    @GetMapping("/getBlogBySearch")
    public String getBlogBySearch(HttpServletRequest request,
                                  @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogBySearch(currentPage, null));
    }


    @ApiOperation(value = "按时间戳获取博客", notes = "按时间戳获取博客")
    @GetMapping("/getBlogByTime")
    public String getBlogByTime(HttpServletRequest request,
                                @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConstants.BLOG_NEW_COUNT);
        return ResultUtil.result(SysConstants.SUCCESS, blogService.getBlogByTime(currentPage, Long.valueOf(blogNewCount)));
    }

    @ApiOperation(value = "获取最热标签", notes = "获取最热标签")
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

    @ApiOperation(value = "获取友情链接", notes = "获取友情链接")
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
    @ApiOperation(value = "增加友情链接点击数", notes = "增加友情链接点击数")
    @GetMapping("/addLinkCount")
    public String addLinkCount(@ApiParam(name = "uid", value = "友情链接UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        log.info("点击友链");
        return linkService.addLinkCount(uid);
    }

    @ApiOperation(value = "获取网站配置", notes = "获取友情链接")
    @GetMapping("/getWebConfig")
    public String getWebConfig() {
        log.info("获取网站配置");
        return ResultUtil.result(SysConstants.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @ApiOperation(value = "获取网站导航栏", notes = "获取网站导航栏")
    @GetMapping("/getWebNavbar")
    public String getWebNavbar() {
        log.info("获取网站导航栏");
        return ResultUtil.result(SysConstants.SUCCESS, webNavbarService.getAllList());
    }

    @BussinessLog(value = "记录访问页面", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation(value = "记录访问页面", notes = "记录访问页面")
    @GetMapping("/recorderVisitPage")
    public String recorderVisitPage(@ApiParam(name = "pageName", value = "页面名称", required = false) @RequestParam(name = "pageName", required = true) String pageName) {

        if (StringUtils.isEmpty(pageName)) {
            return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.INSERT_SUCCESS);
    }
}

