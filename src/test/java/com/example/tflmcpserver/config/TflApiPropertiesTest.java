package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class TflApiPropertiesTest {

    private final Validator validator;

    TflApiPropertiesTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Test
    void validPropertiesPassValidation() {
        TflApiProperties properties = new TflApiProperties("key", "https://api.tfl.gov.uk", 10);

        assertTrue(validator.validate(properties).isEmpty());
        assertEquals("key", properties.key());
    }

    @Test
    void invalidPropertiesFailValidation() {
        TflApiProperties properties = new TflApiProperties("", "", 0);

        assertFalse(validator.validate(properties).isEmpty());
    }
}
