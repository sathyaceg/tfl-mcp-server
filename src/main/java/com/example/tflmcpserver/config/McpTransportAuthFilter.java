package com.example.tflmcpserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class McpTransportAuthFilter extends OncePerRequestFilter {

    private final McpTransportAuthProperties authProperties;
    private final Set<String> protectedPaths;

    public McpTransportAuthFilter(McpTransportAuthProperties authProperties, Set<String> protectedPaths) {
        this.authProperties = authProperties;
        this.protectedPaths = protectedPaths;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!authProperties.enabled() || !isProtectedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String provided = request.getHeader(authProperties.headerName());
        if (authProperties.apiKey().equals(provided)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Unauthorized MCP transport request\"}");
    }

    private boolean isProtectedPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String path = requestUri.startsWith(contextPath)
                ? requestUri.substring(contextPath.length())
                : requestUri;
        return protectedPaths.contains(path);
    }
}
