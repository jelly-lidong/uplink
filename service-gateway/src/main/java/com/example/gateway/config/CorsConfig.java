package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // 允许指定的域名
        corsConfig.addAllowedOrigin("http://localhost:8001");
        corsConfig.addAllowedOrigin("http://localhost:8002");

        // 允许所有请求方法
        corsConfig.addAllowedMethod("*");

        // 允许所有请求头
        corsConfig.addAllowedHeader("*");

        // 允许发送凭证
        corsConfig.setAllowCredentials(true);

        // 设置预检请求的最大缓存时间
        corsConfig.setMaxAge(3600L);

        // 允许路径匹配
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // 为所有路径配置 CORS

        return source;
    }
} 