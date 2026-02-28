package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.tflmcpserver.model.TflStationDataProperties;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class TflStationDataPropertiesTest {

	private final Validator validator;

	TflStationDataPropertiesTest() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		this.validator = validatorFactory.getValidator();
	}

	@Test
	void validPropertiesPassValidation() {
		TflStationDataProperties properties = new TflStationDataProperties("/tmp/station-data");

		assertTrue(validator.validate(properties).isEmpty());
	}

	@Test
	void invalidPropertiesFailValidation() {
		TflStationDataProperties properties = new TflStationDataProperties("");

		assertFalse(validator.validate(properties).isEmpty());
	}
}
