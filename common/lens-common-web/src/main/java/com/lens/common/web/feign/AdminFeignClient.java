package com.lens.common.web.feign;



import com.lens.common.web.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 后台服务feign远程调用
 *
 * @author Lens
 * @date 2020年1月21日22:19:10
 */

@FeignClient(name = "lens-blog-admin-backend", configuration = FeignConfiguration.class)
public interface AdminFeignClient {


    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/systemConfig/getSystemConfig", method = RequestMethod.GET)
    public String getSystemConfig();

}