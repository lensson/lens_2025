package com.lens.blog.admin;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;


import java.util.TimeZone;

/**
 * mogu-admin 启动类
 *
 * @author 陌溪
 * @date 2020年12月31日21:26:04
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableCaching
@EnableRabbit
@EnableFeignClients("com.lens.common.web.feign")
@ComponentScan(basePackages = {
        "com.lens.common.web.config",
        "com.lens.common.web.fallback",
        "com.lens.common.base.utils",
        "com.lens.common.core.utils",
        "com.lens.common.redis.utils",
        "com.lens.blog.admin",
        "com.lens.blog.xo.utils",
        "com.lens.blog.xo.service"
})
public class LensBlogAdminBackend {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(LensBlogAdminBackend.class, args);
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
