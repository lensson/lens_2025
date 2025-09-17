package com.lens.blog.web;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;



import java.util.TimeZone;

@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableCaching
@EnableRabbit
@EnableFeignClients("com.lens.common.web.feign")
@ComponentScan(basePackages = {
        "com.lens.common.db.mybatis.config",
        "com.lens.common.web.config",
        "com.lens.common.web.jwt",
        "com.lens.common.web.fallback",
        "com.lens.common.base.utils",
        "com.lens.common.core.utils",
        "com.lens.common.redis.utils",
        "com.lens.blog.web",
        "com.lens.blog.xo.utils",
        "com.lens.blog.xo.service"})
@OpenAPIDefinition(
        info = @Info(
                title = "Lens Blog Backend",
                version = "1.0",
                description = "Blog Backend Documentation v1.0"))
public class LensBlogBackend {

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(LensBlogBackend.class);
        sa.setAllowCircularReferences(true);
        sa.run(args);
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
