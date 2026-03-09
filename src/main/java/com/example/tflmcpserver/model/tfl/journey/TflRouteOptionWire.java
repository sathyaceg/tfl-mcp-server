package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflRouteOptionWire {

	private String name;
	private String direction;
	private TflIdentifierWire lineIdentifier;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public TflIdentifierWire getLineIdentifier() {
		return lineIdentifier;
	}

	public void setLineIdentifier(TflIdentifierWire lineIdentifier) {
		this.lineIdentifier = lineIdentifier;
	}
}
