package com.lens.blog.web.restapi;


import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.xo.service.BlogService;
import com.lens.common.base.enums.EBehavior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 归档 RestApi
 *
 * @author 陌溪
 * @date 2019年10月24日15:29:35
 */
@RestController
@RequestMapping("/sort")
@Tag(name = "博客归档相关接口", description = "博客归档相关接口")
@Slf4j
public class SortRestApi {

    @Autowired
    BlogService blogService;

    /**
     * 获取归档的信息
     */
    @Operation(summary = "归档", description = "归档")
    @GetMapping("/getSortList")
    public String getSortList() {
        log.info("获取归档日期");
        return blogService.getBlogTimeSortList();
    }

    @BussinessLog(value = "点击归档", behavior = EBehavior.VISIT_SORT)
    @Operation(summary = "通过月份获取文章", description = "通过月份获取文章")
    @GetMapping("/getArticleByMonth")
    public String getArticleByMonth(@Parameter(name = "monthDate", description = "归档的日期", required = false) @RequestParam(name = "monthDate", required = false) String monthDate) {
        log.info("通过月份获取文章列表");
        return blogService.getArticleByMonth(monthDate);
    }
}

