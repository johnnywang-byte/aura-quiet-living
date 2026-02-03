package com.aura.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI auraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Aura Quiet Living API")
                        .description("REST API for Aura E-Commerce Platform powered by Spring AI")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aura Team")
                                .email("dev@aura.com")
                                .url("https://aura.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
