package com.moviequiz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * In the normal dev/prod topology, nginx fronts both the frontend and the
 * backend on a single origin, so the browser never makes a cross-origin
 * request and CORS never comes into play. This configuration only matters
 * if you bypass the reverse proxy during local development - e.g. running
 * `npm run dev` and pointing it straight at http://localhost:8080 instead
 * of through nginx.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
