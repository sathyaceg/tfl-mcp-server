package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.config.TflApiProperties;
import com.example.tflmcpserver.service.JourneyPlannerService;
import com.example.tflmcpserver.tools.JourneyPlannerTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.reactive.function.client.WebClient;

class McpToolConfigTest {

    @Test
    void createsToolCallbackProvider() {
        JourneyPlannerService service = new JourneyPlannerService(
                new TflJourneyClient(WebClient.builder().build(),
                        new TflApiProperties("key", "https://api.tfl.gov.uk", 5)));
        JourneyPlannerTools tools = new JourneyPlannerTools(service);
        McpToolConfig config = new McpToolConfig();

        ToolCallbackProvider provider = config.journeyToolCallbacks(tools);

        assertNotNull(provider);
        assertFalse(provider.getToolCallbacks().length == 0);
    }
}
