package com.lens.platform.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zhenac
 * @Created 5/21/25 11:23 AM
 */

@RestController
@RequestMapping("/echo")
public class EchoController {

    @Value("${spring.application.name}")
    String applicationName;



    @Value("${server.port}")
    private String serverPort;

    @GetMapping
    @Operation(summary = "Calculate application name and port", description = "Returns price for given product with amount")
    public String echo(){
        return applicationName + " run on " + serverPort;
    }


}
