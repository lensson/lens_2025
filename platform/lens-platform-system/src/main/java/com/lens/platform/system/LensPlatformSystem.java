package com.lens.platform.system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

/**
 * @Author zhenac
 * @Created 5/27/25 7:53 AM
 */

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients("com.lens.common.web.feign")
@OpenAPIDefinition(
        info = @Info(
                title = "Lens Platform System",
                version = "1.0",
                description = "Platform System Documentation v1.0"))
public class LensPlatformSystem {


    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(LensPlatformSystem.class, args);
    }
}

