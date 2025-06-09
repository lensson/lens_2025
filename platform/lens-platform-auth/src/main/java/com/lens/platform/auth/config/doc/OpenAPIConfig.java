package com.lens.platform.auth.config.doc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author zhenac
 * @Created 5/21/25 11:28 AM
 */

@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {

//    @Bean
//    public OpenAPI customOpenAPI(
//            @Value("${openapi.service.title}") String serviceTitle,
//            @Value("${openapi.service.version}") String serviceVersion,
//            @Value("${openapi.service.url}") String url) {
//        return new OpenAPI()
//                .servers(List.of(new Server().url(url)))
//                .info(new Info().title(serviceTitle).version(serviceVersion));
//    }

    @Value("${keycloak.url}")
    String authServerUrl;
    @Value("${keycloak.realm}")
    String realm;

    private static final String OAUTH_SCHEME_NAME = "keycloak_oAuth_security_schema";

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.title}") String serviceTitle,
            @Value("${openapi.service.version}") String serviceVersion,
            @Value("${openapi.service.url}") String url) {
        return new OpenAPI().
                components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .servers(List.of(new Server().url(url)))
                .info(new Info().title(serviceTitle)
                        .description("System Platform API")
                        .version(serviceVersion));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createAuthorizationCodeFlow();
        return new OAuthFlows().implicit(flow);
    }

    private OAuthFlow createAuthorizationCodeFlow() {
        return new OAuthFlow()
                .authorizationUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
                .scopes(new Scopes().addString("VIEW", "read data")
                        .addString("ADMIN", "modify data"));
    }
}
