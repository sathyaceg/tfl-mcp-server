package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

class McpTransportAuthPropertiesTest {

    private final Validator validator;

    McpTransportAuthPropertiesTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Test
    void validPropertiesPassValidation() {
        McpTransportAuthProperties properties =
                new McpTransportAuthProperties(true, "X-MCP-API-KEY", "secret", List.of("/mcp"));

        assertTrue(validator.validate(properties).isEmpty());
        assertEquals("X-MCP-API-KEY", properties.headerName());
    }

    @Test
    void invalidPropertiesFailValidation() {
        McpTransportAuthProperties properties =
                new McpTransportAuthProperties(true, "", "", List.of());

        assertFalse(validator.validate(properties).isEmpty());
    }
}
