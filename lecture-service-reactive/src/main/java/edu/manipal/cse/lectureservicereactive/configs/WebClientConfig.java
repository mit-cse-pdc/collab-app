package edu.manipal.cse.lectureservicereactive.configs;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient beans.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient.Builder bean that is load-balanced.
     * This allows WebClient instances created from this builder to resolve
     * service names using the configured
     * service discovery mechanism (e.g., Eureka).
     *
     * @return A load-balanced WebClient.Builder instance.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

}