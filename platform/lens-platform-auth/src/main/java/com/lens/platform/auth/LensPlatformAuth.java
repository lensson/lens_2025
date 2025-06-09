package com.lens.platform.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



import java.util.TimeZone;

/**
 * @Author zhenac
 * @Created 5/20/25 2:17 PM
 */

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients("com.lens.common.web.feign")
@OpenAPIDefinition(
        info = @Info(
                title = "Lens Platform Auth",
                version = "1.0",
                description = "Auth Gateway Documentation v1.0"))
public class LensPlatformAuth {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(LensPlatformAuth.class, args);
    }
}
