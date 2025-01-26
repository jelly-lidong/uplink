package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("task_route", r -> r.path("/task/**")
                        .uri("http://localhost:8002"))
                .route("resource_route", r -> r.path("/resource/**")
                        .uri("http://localhost:8001"))
                .build();
    }
} 