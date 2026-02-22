package com.example.tflmcpserver;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.tflmcpserver.config.TflApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

class TflMcpServerApplicationTest {

    @Test
    void enablesTflApiPropertiesBinding() {
        EnableConfigurationProperties annotation =
                TflMcpServerApplication.class.getAnnotation(EnableConfigurationProperties.class);

        assertNotNull(annotation);
        assertArrayEquals(new Class<?>[]{TflApiProperties.class}, annotation.value());
    }
}
