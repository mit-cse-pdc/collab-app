package edu.manipal.cse.gatewayservice.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
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
                .description("Gateway Server URL in Development environment");

        Contact contact = new Contact()
                .name("PDC Microservices Team")
                .email("support@pdcservices.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("PDC Microservices API Gateway")
                .version("1.0")
                .contact(contact)
                .description("Unified API Gateway for PDC Microservices: Auth, User, and Master Data Services")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }

    @Bean
    public GroupedOpenApi authServiceApi() {
        return GroupedOpenApi.builder()
                .group("Auth Service")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceApi() {
        return GroupedOpenApi.builder()
                .group("User Service")
                .pathsToMatch("/faculty/**", "/students/**")
                .build();
    }

    @Bean
    public GroupedOpenApi masterDataServiceApi() {
        return GroupedOpenApi.builder()
                .group("Master Data Service")
                .pathsToMatch("/schools/**", "/specializations/**", "/courses/**", "/enrollments/**", "/faculty-courses/**")
                .build();
    }
}
