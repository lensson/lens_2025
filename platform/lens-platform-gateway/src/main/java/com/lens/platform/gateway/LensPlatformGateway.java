package com.lens.platform.gateway;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;


@EnableDiscoveryClient
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Lens Platform Gateway",
                version = "1.0",
                description = "Platform Gateway Documentation v1.0"))
public class LensPlatformGateway {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(LensPlatformGateway.class, args);
    }

}
