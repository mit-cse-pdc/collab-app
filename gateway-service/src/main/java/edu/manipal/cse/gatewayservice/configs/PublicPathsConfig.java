package edu.manipal.cse.gatewayservice.configs;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;

/**
 * Configuration properties for managing public paths that don't require authentication
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gateway.security")
public class PublicPathsConfig {
    private List<String> publicPaths = new ArrayList<>();
}