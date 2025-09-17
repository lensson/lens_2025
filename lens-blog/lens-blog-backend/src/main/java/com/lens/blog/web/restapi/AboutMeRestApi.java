package com.lens.blog.web.restapi;


import com.lens.blog.web.annotion.log.BussinessLog;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.AdminService;
import com.lens.blog.xo.service.WebConfigService;
import com.lens.common.base.enums.EBehavior;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关于我 RestApi
 *
 * @author 陌溪
 * @date 2018年11月12日14:51:54
 */
@RestController
@RequestMapping("/about")
@Tag(name = "关于我相关接口", description = "关于我相关接口")
@Slf4j
public class AboutMeRestApi {

    @Autowired
    AdminService adminService;

    @Autowired
    WebConfigService webConfigService;

    /**
     * 获取关于我的信息
     */
    @BussinessLog(value = "关于我", behavior = EBehavior.VISIT_PAGE)
    @Operation(summary = "关于我", description = "关于我")
    @GetMapping("/getMe")
    public String getMe() {

        log.info("获取关于我的信息");
        return ResultUtil.result(SysConstants.SUCCESS, adminService.getAdminByUser(SysConstants.ADMIN));
    }

    @Operation(summary = "获取联系方式", description = "获取联系方式")
    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtil.result(SysConstants.SUCCESS, webConfigService.getWebConfigByShowList());
    }

}

