package com.lens.platform.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @Author zhenac
 * @Created 5/26/25 1:39 PM
 */

@RestController
@RequestMapping("/caller")
public class CallerController {
    private WebClient webClient;

    public CallerController(WebClient webClient) {
        this.webClient = webClient;
    }

    @Operation(summary = "Invoke system callme ping", description = "Return http://172.28.0.1:8842/callme/ping")
    @PreAuthorize("hasAuthority('SCOPE_TEST')")
    @GetMapping("/ping")
    public String ping() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String scopes = webClient
                .get()
                .uri("http://172.28.0.1:8842/callme/ping")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return "Callme scopes: " + scopes;
    }
}
