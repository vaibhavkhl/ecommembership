package com.example.demo.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The Vite dev server proxies /api to this app, so CORS isn't strictly needed in that setup -
 * but this allows the frontend to also call the API directly (e.g. `vite preview`, or a
 * frontend served from a different origin) without a proxy in front of it. Note that Spring's
 * CORS filter validates the Origin header (sent by browsers on non-GET/HEAD fetches even for
 * same-origin requests) against this allow-list regardless of whether the request is actually
 * cross-origin - so this must include the production URL too, not just the local dev one.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }
}
