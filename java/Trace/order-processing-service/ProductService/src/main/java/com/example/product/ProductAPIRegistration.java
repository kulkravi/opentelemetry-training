package com.example.product;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.opentelemetry.sdk.OpenTelemetrySdk;

@Configuration
public class ProductAPIRegistration implements WebMvcConfigurer {
    OpenTelemetrySdk openTelemetrySdk;
    ProductAPIRegistration (OpenTelemetryService openTelemetryService) {
        openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) { 
        registry.addInterceptor(new ProductInboundInterceptor(openTelemetrySdk)); 
    } 
}