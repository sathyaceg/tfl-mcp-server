package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.tflmcpserver.model.McpTransportAuthProperties;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class McpTransportSecurityConfigTest {

	@Test
	void registersAuthFilterWithOrderOne() {
		McpTransportAuthProperties authProperties = new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret",
				List.of("/mcp"));
		McpTransportSecurityConfig config = new McpTransportSecurityConfig();

		FilterRegistrationBean<McpTransportAuthFilter> registration = config
				.mcpTransportAuthFilterRegistration(authProperties);

		assertEquals(1, registration.getOrder());
		assertInstanceOf(McpTransportAuthFilter.class, registration.getFilter());
	}

	@Test
	void registeredFilterProtectsConfiguredPath() throws ServletException, IOException {
		McpTransportAuthProperties authProperties = new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret",
				List.of("/mcp"));
		McpTransportSecurityConfig config = new McpTransportSecurityConfig();
		McpTransportAuthFilter filter = config.mcpTransportAuthFilterRegistration(authProperties).getFilter();

		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/mcp");
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, new MockFilterChain());

		assertEquals(401, response.getStatus());
	}
}
