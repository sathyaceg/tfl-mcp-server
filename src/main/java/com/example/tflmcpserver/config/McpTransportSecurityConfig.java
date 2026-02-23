package com.example.tflmcpserver.config;

import java.util.Set;

import com.example.tflmcpserver.model.McpTransportAuthProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpTransportSecurityConfig {

    @Bean
    public FilterRegistrationBean<McpTransportAuthFilter> mcpTransportAuthFilterRegistration(
            McpTransportAuthProperties authProperties) {

        Set<String> protectedPaths = Set.copyOf(authProperties.protectedPaths());

        FilterRegistrationBean<McpTransportAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new McpTransportAuthFilter(authProperties, protectedPaths));
        registration.setOrder(1);
        return registration;
    }
}
