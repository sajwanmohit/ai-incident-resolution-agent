package com.mohit.aiagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiCorsConfig implements WebMvcConfigurer {

    private static final String LOCAL_ORIGIN = "http://localhost:5173";
    private static final String VERCEL_PRODUCTION_ORIGIN = "https://ai-incident-dashboard-ashy.vercel.app";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(LOCAL_ORIGIN, VERCEL_PRODUCTION_ORIGIN)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept")
                .maxAge(3600L);
    }
}
