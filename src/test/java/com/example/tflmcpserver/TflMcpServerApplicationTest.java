package com.example.tflmcpserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.tflmcpserver.config.McpTransportAuthProperties;
import com.example.tflmcpserver.model.TflApiProperties;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

class TflMcpServerApplicationTest {

    @Test
    void enablesConfigurationPropertiesBinding() {
        EnableConfigurationProperties annotation =
                TflMcpServerApplication.class.getAnnotation(EnableConfigurationProperties.class);

        assertNotNull(annotation);
        assertTrue(Arrays.asList(annotation.value()).contains(TflApiProperties.class));
        assertTrue(Arrays.asList(annotation.value()).contains(McpTransportAuthProperties.class));
    }
}
