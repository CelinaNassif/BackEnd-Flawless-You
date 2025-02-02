package com.flawlessyou.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "FlawlessYou API",
        version = "1.0",
        description = "API Documentation for FlawlessYou Project",
        contact = @Contact(
            name = "Your Name",
            email = "contact@example.com"
        )
    ),
    servers = {
        @Server(
            description = "Local ENV",
            url = "http://localhost:8080"
        )
    }
)
public class OpenApiConfig {
}