package com.lens.platform.system.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/echo")
public class EchoController {
    @Value("${spring.application.name}")
    String applicationName;



    @Value("${server.port}")
    private String serverPort;

    @GetMapping("echo")
    public String echo(){
        return applicationName + " run on " + serverPort;
    }
}