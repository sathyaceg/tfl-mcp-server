package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.McpTransportAuthProperties;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class McpTransportAuthFilterTest {

    @Test
    void allowsRequestWhenApiKeyMatches() throws ServletException, IOException {
        McpTransportAuthProperties properties =
                new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret", List.of("/mcp"));
        McpTransportAuthFilter filter = new McpTransportAuthFilter(properties, Set.of("/mcp"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/mcp");
        request.addHeader("X-MCP-API-KEY", "secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(200, response.getStatus());
    }

    @Test
    void rejectsProtectedPathWhenApiKeyMissing() throws ServletException, IOException {
        McpTransportAuthProperties properties =
                new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret", List.of("/mcp"));
        McpTransportAuthFilter filter = new McpTransportAuthFilter(properties, Set.of("/mcp"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/mcp");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
    }

    @Test
    void bypassesAuthForNonMcpPath() throws ServletException, IOException {
        McpTransportAuthProperties properties =
                new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret", List.of("/mcp"));
        McpTransportAuthFilter filter = new McpTransportAuthFilter(properties, Set.of("/mcp"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(200, response.getStatus());
    }
}
