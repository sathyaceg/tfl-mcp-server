package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflRouteOption {

	private String name;
	private String direction;
	private TflIdentifier lineIdentifier;

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

	public TflIdentifier getLineIdentifier() {
		return lineIdentifier;
	}

	public void setLineIdentifier(TflIdentifier lineIdentifier) {
		this.lineIdentifier = lineIdentifier;
	}
}
