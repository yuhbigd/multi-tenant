package com.example.multitenants.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.multitenants.util.ExistedTenants;
import com.example.multitenants.util.interceptor.TenantInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebMvcConfig {
    @Bean
    WebMvcConfigurer webMvcConfigurer(ObjectMapper objectMapper, ExistedTenants existedTenants) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new TenantInterceptor(objectMapper, existedTenants));
            }
        };
    }
}
