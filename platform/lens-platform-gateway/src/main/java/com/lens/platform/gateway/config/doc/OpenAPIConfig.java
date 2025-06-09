package com.lens.platform.gateway.config.doc;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author zhenac
 * @Created 5/19/25 2:41 PM
 */

@OpenAPIDefinition
@Configuration
@SecurityScheme(name = "keycloak_oAuth_security_schema",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode =
        @OAuthFlow(
                authorizationUrl = "${keycloak.url}/realms/${keycloak.realm}/protocol/openid-connect/auth",
                scopes = {
                        @OAuthScope(name = "VIEW", description = "read scope"),
                        @OAuthScope(name = "ADMIN", description = "write scope")
                })
        ))
public class OpenAPIConfig {

//    private static final String OAUTH_SCHEME_NAME = "keycloak_oAuth_security_schema";

    @Bean
    public OpenAPI customOpenAPI(@Value("${openapi.service.title}") String serviceTitle, @Value("${openapi.service.version}") String serviceVersion, @Value("${openapi.service.url}") String url) {
        return new OpenAPI().servers(List.of(new Server().url(url))).info(new Info().title(serviceTitle).version(serviceVersion));
    }

}
