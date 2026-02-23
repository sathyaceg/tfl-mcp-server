package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.tflmcpserver.service.JourneyPlannerService;
import com.example.tflmcpserver.tools.JourneyPlannerTools;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.tool.ToolCallbackProvider;

class McpToolConfigTest {

	@Test
	void createsToolCallbackProvider() {
		JourneyPlannerService service = Mockito.mock(JourneyPlannerService.class);
		JourneyPlannerTools tools = new JourneyPlannerTools(service);
		McpToolConfig config = new McpToolConfig();

		ToolCallbackProvider provider = config.journeyToolCallbacks(tools);

		assertNotNull(provider);
		assertFalse(provider.getToolCallbacks().length == 0);
	}
}
