package com.pdc.gatewayservice.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

//    private final JwtAuthenticationFilter jwtFilter;
//
//    @Value("${services.college-management.url}")
//    private String collegeManagementUrl;
//
//    @Value("${services.auth.url}")
//    private String authUrl;
//
//    @Value("${services.user.url}")
//    private String userUrl;

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // Auth Service Routes
//                .route("auth_service_route", r -> r
//                        .path("/auth/**")
//                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
//                        .uri(authUrl))
//
//                // College Management Service - Public Routes (No JWT check)
//                .route("departments_public_route", r -> r
//                        .path("/departments/**", "/semesters/**")
//                        .uri(collegeManagementUrl))
//
//                // Faculty Public Routes
//                .route("faculty_public_route", r -> r
//                        .path("/faculty/**")
//                        .uri(userUrl))
//
//                .build();
//    }
}