package com.pdc.questionbankservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Server URL in Development environment");

        Contact contact = new Contact()
                .name("Question Bank Service Team")
                .email("support@questionbankservice.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Question Bank Service API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for managing questions.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}