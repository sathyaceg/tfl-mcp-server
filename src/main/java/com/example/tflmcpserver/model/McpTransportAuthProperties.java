package com.example.tflmcpserver.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "mcp.transport.auth")
public record McpTransportAuthProperties(boolean enabled, @NotBlank String headerName, @NotBlank String apiKey,
		@NotEmpty List<@NotBlank String> protectedPaths) {
}
